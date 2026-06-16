import React, { useEffect, useState } from 'react';
import ReactApexChart from 'react-apexcharts';
import { useAuth } from '../context/AuthContext';
import { backEndService } from '../services/api';
import type { IExtrato, IBoleto } from '../types';
import { FaFilePdf, FaArrowUp, FaArrowDown, FaWallet, FaSpinner } from 'react-icons/fa';
import type { ApexOptions } from 'apexcharts';

const Dashboard: React.FC = () => {
  const { user, isAdminOrSindico } = useAuth();
  
  // Estados
  const [extratos, setExtratos] = useState<IExtrato[]>([]);
  const [boletos, setBoletos] = useState<IBoleto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Estados dos filtros
  const [daysFilter, setDaysFilter] = useState<number>(isAdminOrSindico() ? 90 : 30);

  useEffect(() => {
    const fetchData = async (): Promise<void> => {
      try {
        setLoading(true);
        setError(null);
        
        // Chamadas paralelas para obter Extratos e Boletos
        const [extratosData, boletosData] = await Promise.all([
          backEndService.get<IExtrato[]>('/extratos'),
          backEndService.get<IBoleto[]>('/boletos'),
        ]);

        setExtratos(extratosData);
        setBoletos(boletosData);
      } catch (err: any) {
        console.error(err);
        setError('Ocorreu um erro ao carregar os dados. Verifique a conexão.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [isAdminOrSindico]);

  // Função auxiliar de baixar PDF a partir de base64
  const handleDownloadPdf = (base64String: string | null, nossoNumero: string): void => {
    if (!base64String) {
      alert('Arquivo PDF não disponível para este boleto.');
      return;
    }

    try {
      const linkSource = `data:application/pdf;base64,${base64String}`;
      const downloadLink = document.createElement('a');
      const fileName = `Boleto_${nossoNumero}.pdf`;
      
      downloadLink.href = linkSource;
      downloadLink.download = fileName;
      downloadLink.click();
    } catch (err) {
      console.error(err);
      alert('Erro ao tentar baixar o arquivo PDF.');
    }
  };

  // Filtragem dos Extratos baseada no filtro de dias
  const filterExtratosByDays = (list: IExtrato[], days: number): IExtrato[] => {
    const limitDate = new Date();
    limitDate.setDate(limitDate.getDate() - days);
    
    return list.filter((item) => {
      const itemDate = new Date(item.dtInclusao);
      return itemDate >= limitDate;
    });
  };

  const filteredExtratos = filterExtratosByDays(extratos, daysFilter);

  // Cálculo das Métricas
  const totalCredits = filteredExtratos
    .filter((item) => item.tipoOperacao === 'C')
    .reduce((sum, item) => sum + item.valorTransacao, 0);

  const totalDebits = filteredExtratos
    .filter((item) => item.tipoOperacao === 'D')
    .reduce((sum, item) => sum + item.valorTransacao, 0);

  const balance = totalCredits - totalDebits;

  // Filtragem de Boletos
  // - Usuário Comum: Seus boletos do último ano (365 dias)
  // - Admin/Sindico: Todos os boletos dos últimos 60 dias
  const getFilteredBoletos = (): IBoleto[] => {
    const limitDate = new Date();
    
    if (isAdminOrSindico()) {
      limitDate.setDate(limitDate.getDate() - 60);
      return boletos.filter((b) => {
        const boletoDate = new Date(b.dtEmissao);
        return boletoDate >= limitDate;
      });
    } else {
      limitDate.setDate(limitDate.getDate() - 365);
      return boletos.filter((b) => {
        const isOwnBoleto = b.usuario && b.usuario.id === user?.userId;
        const boletoDate = new Date(b.dtEmissao);
        return isOwnBoleto && boletoDate >= limitDate;
      });
    }
  };

  const displayBoletos = getFilteredBoletos();

  // Dados do Gráfico 1: Entrada vs Saída por data
  const getChart1Data = () => {
    // Agrupa por data
    const grouped: { [key: string]: { credits: number; debits: number } } = {};
    
    filteredExtratos.forEach((item) => {
      const dateStr = item.dtInclusao; // formato YYYY-MM-DD
      if (!grouped[dateStr]) {
        grouped[dateStr] = { credits: 0, debits: 0 };
      }
      if (item.tipoOperacao === 'C') {
        grouped[dateStr].credits += item.valorTransacao;
      } else {
        grouped[dateStr].debits += item.valorTransacao;
      }
    });

    // Ordenar chaves cronologicamente
    const sortedDates = Object.keys(grouped).sort();
    
    const creditsSeries = sortedDates.map((date) => Number(grouped[date].credits.toFixed(2)));
    const debitsSeries = sortedDates.map((date) => Number(grouped[date].debits.toFixed(2)));

    // Formata datas de forma amigável (DD/MM)
    const categories = sortedDates.map((dateStr) => {
      const [, month, day] = dateStr.split('-');
      return `${day}/${month}`;
    });

    return {
      series: [
        { name: 'Receitas (Créditos)', data: creditsSeries },
        { name: 'Despesas (Débitos)', data: debitsSeries },
      ],
      categories,
    };
  };

  const chart1Info = getChart1Data();

  // Gráfico 1: Opções do ApexCharts
  const cashFlowChartOptions: ApexOptions = {
    chart: {
      type: 'bar',
      height: 350,
      toolbar: { show: false },
    },
    colors: ['#10b981', '#e53e3e'],
    plotOptions: {
      bar: {
        horizontal: false,
        columnWidth: '55%',
        borderRadius: 4,
      },
    },
    dataLabels: { enabled: false },
    stroke: {
      show: true,
      width: 2,
      colors: ['transparent'],
    },
    xaxis: {
      categories: chart1Info.categories,
      title: { text: 'Período' },
    },
    yaxis: {
      title: { text: 'Valor (R$)' },
    },
    fill: { opacity: 1 },
    tooltip: {
      y: {
        formatter: (val) => `R$ ${val.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
      },
    },
    legend: { position: 'top' },
  };

  // Dados do Gráfico 2: Top 5 Ofensores de Débito nos últimos 90 dias (Apenas Admin)
  const getTop5Debtors = () => {
    // Filtra apenas débitos (D)
    const debitsList = filteredExtratos.filter((item) => item.tipoOperacao === 'D');
    
    // Agrupa valores por receptor
    const grouped: { [key: string]: number } = {};
    
    debitsList.forEach((item) => {
      const name = item.nomeRecebedor || item.descricao || 'Outros';
      grouped[name] = (grouped[name] || 0) + item.valorTransacao;
    });

    // Ordenar e pegar os 5 maiores
    const sorted = Object.entries(grouped)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5);

    const labels = sorted.map((item) => item[0]);
    const series = sorted.map((item) => Number(item[1].toFixed(2)));

    return { labels, series };
  };

  const top5Info = getTop5Debtors();

  // Gráfico 2: Opções do ApexCharts
  const topDebtorsChartOptions: ApexOptions = {
    chart: {
      type: 'donut',
      height: 350,
    },
    labels: top5Info.labels,
    colors: ['#e53e3e', '#f59e0b', '#3c50e0', '#8b5cf6', '#ec4899'],
    legend: { position: 'bottom' },
    responsive: [
      {
        breakpoint: 480,
        options: {
          chart: { width: 200 },
          legend: { position: 'bottom' },
        },
      },
    ],
    tooltip: {
      y: {
        formatter: (val) => `R$ ${val.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`,
      },
    },
  };

  if (loading) {
    return (
      <div className="d-flex flex-column justify-content-center align-items-center w-100 py-5">
        <FaSpinner className="spin text-primary mb-3" style={{ fontSize: '3rem' }} />
        <p className="text-muted">Carregando informações do Painel...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="alert alert-danger" role="alert">
        {error}
      </div>
    );
  }

  return (
    <div>
      {/* Cabeçalho do Dashboard com Filtro de Período */}
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
          <h2 className="mb-1">Dashboard</h2>
          <p className="text-muted mb-0">Olá, {user?.userName}. Veja o resumo do condomínio.</p>
        </div>

        {/* Se for Admin/Síndico, oferece a opção de trocar a visualização de dias */}
        {isAdminOrSindico() && (
          <div className="btn-group shadow-sm">
            <button 
              type="button" 
              className={`btn btn-sm ${daysFilter === 30 ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setDaysFilter(30)}
            >
              30 Dias
            </button>
            <button 
              type="button" 
              className={`btn btn-sm ${daysFilter === 60 ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setDaysFilter(60)}
            >
              60 Dias
            </button>
            <button 
              type="button" 
              className={`btn btn-sm ${daysFilter === 90 ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setDaysFilter(90)}
            >
              90 Dias
            </button>
          </div>
        )}
      </div>

      {/* Cards de Métricas */}
      <div className="row g-4 mb-4">
        <div className="col-md-4">
          <div className="card-metric shadow-sm">
            <div>
              <p className="card-metric-title">Receitas ({daysFilter} dias)</p>
              <h3 className="card-metric-value">
                R$ {totalCredits.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon credit">
              <FaArrowUp />
            </div>
          </div>
        </div>
        
        <div className="col-md-4">
          <div className="card-metric shadow-sm">
            <div>
              <p className="card-metric-title">Despesas ({daysFilter} dias)</p>
              <h3 className="card-metric-value text-danger">
                R$ {totalDebits.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon debit">
              <FaArrowDown />
            </div>
          </div>
        </div>

        <div className="col-md-4">
          <div className="card-metric shadow-sm">
            <div>
              <p className="card-metric-title">Saldo Consolidado</p>
              <h3 className={`card-metric-value ${balance >= 0 ? 'text-success' : 'text-danger'}`}>
                R$ {balance.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon balance">
              <FaWallet />
            </div>
          </div>
        </div>
      </div>

      {/* Gráficos de Fluxo de Caixa */}
      <div className="row g-4 mb-4">
        <div className={isAdminOrSindico() ? 'col-lg-8' : 'col-12'}>
          <div className="card-content">
            <div className="card-content-header">
              <h5 className="card-content-title">Fluxo de Caixa (Débitos vs Créditos) - Últimos {daysFilter} Dias</h5>
            </div>
            <div className="card-content-body">
              {chart1Info.series[0].data.length > 0 ? (
                <ReactApexChart 
                  options={cashFlowChartOptions} 
                  series={chart1Info.series} 
                  type="bar" 
                  height={350} 
                />
              ) : (
                <div className="py-5 text-center text-muted">Sem movimentações no período filtrado.</div>
              )}
            </div>
          </div>
        </div>

        {isAdminOrSindico() && (
          <div className="col-lg-4">
            <div className="card-content">
              <div className="card-content-header">
                <h5 className="card-content-title">Maiores Ofensores de Débito</h5>
              </div>
              <div className="card-content-body">
                {top5Info.series.length > 0 ? (
                  <ReactApexChart 
                    options={topDebtorsChartOptions} 
                    series={top5Info.series} 
                    type="donut" 
                    height={350} 
                  />
                ) : (
                  <div className="py-5 text-center text-muted">Sem despesas registradas no período.</div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Tabela de Boletos */}
      <div className="card-content">
        <div className="card-content-header">
          <div>
            <h5 className="card-content-title">
              {isAdminOrSindico() ? 'Boletos Registrados (Últimos 60 dias)' : 'Meus Boletos (Último Ano)'}
            </h5>
            <p className="text-muted small mb-0">Listagem de cobranças pendentes e pagas.</p>
          </div>
        </div>
        <div className="card-content-body p-0">
          <div className="table-responsive">
            <table className="table table-custom">
              <thead>
                <tr>
                  <th>Nosso Número</th>
                  {isAdminOrSindico() && <th>Morador</th>}
                  {isAdminOrSindico() && <th>Unidade</th>}
                  <th>Vencimento</th>
                  <th>Emissão</th>
                  <th>Valor</th>
                  <th>Situação</th>
                  <th>Ações</th>
                </tr>
              </thead>
              <tbody>
                {displayBoletos.length > 0 ? (
                  displayBoletos.map((boleto) => (
                    <tr key={boleto.id}>
                      <td className="fw-semibold">{boleto.nossoNumero || 'N/A'}</td>
                      {isAdminOrSindico() && <td>{boleto.usuario?.nomeUsuario || 'N/A'}</td>}
                      {isAdminOrSindico() && (
                        <td>
                          {boleto.usuario?.unidade 
                            ? `AP ${boleto.usuario.unidade.numeroUnidade}` 
                            : 'N/A'}
                        </td>
                      )}
                      <td>{new Date(boleto.dtVencimento).toLocaleDateString('pt-BR')}</td>
                      <td>{new Date(boleto.dtEmissao).toLocaleDateString('pt-BR')}</td>
                      <td className="fw-bold">
                        R$ {boleto.valor.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                      </td>
                      <td>
                        <span className={`badge-custom ${(boleto.txSituacao || 'aberto').toLowerCase()}`}>
                          {boleto.txSituacao || 'ABERTO'}
                        </span>
                      </td>
                      <td>
                        <button
                          type="button"
                          className="btn btn-outline-danger btn-sm d-inline-flex align-items-center gap-1"
                          onClick={() => handleDownloadPdf(boleto.arquivopdf, boleto.nossoNumero)}
                          title="Download PDF"
                        >
                          <FaFilePdf />
                          <span className="d-none d-sm-inline">PDF</span>
                        </button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={isAdminOrSindico() ? 8 : 6} className="text-center py-4 text-muted">
                      Nenhum boleto encontrado no período correspondente.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
