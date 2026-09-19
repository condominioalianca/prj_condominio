import React, { useEffect, useState } from 'react';
import ReactApexChart from 'react-apexcharts';
import { useAuth } from '../context/AuthContext';
import { backEndService } from '../services/api';
import type { IExtrato, IBoleto, ISaldo } from '../types';
import { FaFilePdf, FaArrowUp, FaArrowDown, FaWallet, FaSpinner, FaPiggyBank } from 'react-icons/fa';
import type { ApexOptions } from 'apexcharts';

const Dashboard: React.FC = () => {
  const { user, isAdminOrSindico, hasPerfilAtrelado } = useAuth();
  
  // Estados
  const [extratos, setExtratos] = useState<IExtrato[]>([]);
  const [boletos, setBoletos] = useState<IBoleto[]>([]);
  const [saldoAtual, setSaldoAtual] = useState<ISaldo | null>(null);
  const [hasUnidade, setHasUnidade] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Obter mês atual no formato YYYY-MM
  const getCurrentYearMonth = (): string => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    return `${year}-${month}`;
  };

  // Estados dos filtros
  const [selectedMonth, setSelectedMonth] = useState<string>(getCurrentYearMonth());

  // Gera os últimos 24 meses em ordem decrescente (do mais recente para o mais antigo)
  const monthOptions = React.useMemo(() => {
    const options: { value: string; label: string }[] = [];
    const now = new Date();
    for (let i = 0; i < 24; i++) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const year = d.getFullYear();
      const monthNumber = String(d.getMonth() + 1).padStart(2, '0');
      const value = `${year}-${monthNumber}`;

      const monthName = d.toLocaleDateString('pt-BR', { month: 'long' });
      const capitalizedMonth = monthName.charAt(0).toUpperCase() + monthName.slice(1);
      const label = `${capitalizedMonth}/${year}`;

      options.push({ value, label });
    }
    return options;
  }, []);

  const currentMonthLabel = React.useMemo(() => {
    return monthOptions.find((opt) => opt.value === selectedMonth)?.label || selectedMonth;
  }, [monthOptions, selectedMonth]);

  useEffect(() => {
    const fetchData = async (): Promise<void> => {
      try {
        setLoading(true);
        setError(null);
        
        // Chamadas paralelas para obter Extratos, Boletos, Saldo e Perfil Completo do Usuário
        const promises: [Promise<IExtrato[]>, Promise<IBoleto[]>, Promise<ISaldo | null>, Promise<any>] = [
          backEndService.get<IExtrato[]>('/extratos'),
          backEndService.get<IBoleto[]>('/boletos'),
          (user && hasPerfilAtrelado())
            ? backEndService.get<ISaldo>('/saldo/atual').catch(() => null) 
            : Promise.resolve(null),
          user?.userId ? backEndService.get<any>(`/usuarios/${user.userId}`).catch(() => null) : Promise.resolve(null)
        ];

        const [extratosData, boletosData, saldoData, userData] = await Promise.all(promises);

        setExtratos(extratosData);
        setBoletos(boletosData);
        setSaldoAtual(saldoData);

        // Se não for Admin/Síndico, verifica se possui unidade associada
        if (!isAdminOrSindico()) {
          if (userData && !userData.unidade) {
            setHasUnidade(false);
          } else {
            setHasUnidade(true);
          }
        } else {
          setHasUnidade(true);
        }
      } catch (err: any) {
        console.error(err);
        setError('Ocorreu um erro ao carregar os dados. Verifique a conexão.');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [isAdminOrSindico, hasPerfilAtrelado, user]);

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

  // Filtragem dos Extratos baseada no mês selecionado (do dia 01 ao último dia do mês)
  const filterExtratosByMonth = (list: IExtrato[], monthKey: string): IExtrato[] => {
    return list.filter((item) => {
      const rawDate = item.dtTransacao || item.dtInclusao;
      if (!rawDate) return false;
      const cleanDate = rawDate.split('T')[0].split(' ')[0];
      return cleanDate.slice(0, 7) === monthKey;
    });
  };

  const filteredExtratos = filterExtratosByMonth(extratos, selectedMonth);

  // Cálculo das Métricas
  const totalCredits = hasUnidade
    ? filteredExtratos
        .filter((item) => item.tipoOperacao === 'C')
        .reduce((sum, item) => sum + item.valorTransacao, 0)
    : 0;

  const totalDebits = hasUnidade
    ? filteredExtratos
        .filter((item) => item.tipoOperacao === 'D')
        .reduce((sum, item) => sum + item.valorTransacao, 0)
    : 0;

  const fundoDeCaixa = totalCredits - totalDebits;

  // Filtragem de Boletos
  // - Usuário Comum: Seus boletos do último ano (365 dias)
  // - Admin/Sindico: Todos os boletos dos últimos 90 dias (ordenados pelos últimos gerados)
  const getFilteredBoletos = (): IBoleto[] => {
    if (!hasUnidade) {
      return [];
    }
    const limitDate = new Date();
    
    if (isAdminOrSindico()) {
      limitDate.setDate(limitDate.getDate() - 90);
      return boletos
        .filter((b) => {
          const boletoDate = new Date(b.dtEmissao);
          return boletoDate >= limitDate;
        })
        .sort((a, b) => b.id - a.id); // Ordenados pelos últimos gerados (Desc)
    } else {
      limitDate.setDate(limitDate.getDate() - 365);
      return boletos
        .filter((b) => {
          const isOwnBoleto = b.usuario && (b.usuario.id === user?.userId || b.usuario.idUsuario === user?.userId);
          const boletoDate = new Date(b.dtEmissao);
          return isOwnBoleto && boletoDate >= limitDate;
        })
        .sort((a, b) => b.id - a.id);
    }
  };

  const displayBoletos = getFilteredBoletos();

  // Dados do Gráfico 1: Entrada vs Saída por data
  const getChart1Data = () => {
    // Agrupa por data
    const grouped: { [key: string]: { credits: number; debits: number } } = {};
    
    filteredExtratos.forEach((item) => {
      const rawDate = item.dtTransacao || item.dtInclusao;
      if (!rawDate) return;
      const dateStr = rawDate.split('T')[0].split(' ')[0]; // formato YYYY-MM-DD
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

  // Dados do Gráfico 2: Top 5 Ofensores de Débito no mês selecionado
  const getTop5Debtors = () => {
    // Filtra apenas débitos (D)
    const debitsList = filteredExtratos.filter((item) => item.tipoOperacao === 'D');
    
    // Agrupa valores por receptor
    const grouped: { [key: string]: number } = {};
    
    debitsList.forEach((item) => {
      const name = item.categoriaGasto?.descricao || item.nomeRecebedor || item.descricao || 'Outros';
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
      {!hasUnidade && (
        <div className="alert alert-warning border-0 shadow-sm mb-4 p-3" role="alert">
          <strong>Atenção:</strong> Seu cadastro está ativo, mas você ainda não tem uma unidade vinculada ao condomínio. 
          Entre em contato com o Síndico ou Administrador para vincular sua unidade e acessar as informações de cobrança, extrato e saldos.
        </div>
      )}

      {/* Cabeçalho do Dashboard com Filtro de Mês */}
      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
          <h2 className="mb-1">Dashboard</h2>
          <p className="text-muted mb-0">Olá, {user?.userName}. Veja o resumo do condomínio.</p>
        </div>

        {/* Dropbox de seleção de Mês/Ano para Síndico e Usuário */}
        <div className="d-flex align-items-center gap-2">
          <label htmlFor="selectMesDashboard" className="fw-semibold text-muted small mb-0 d-none d-sm-inline">
            Mês:
          </label>
          <select
            id="selectMesDashboard"
            className="form-select form-select-sm shadow-sm"
            style={{ minWidth: '190px', fontWeight: 500 }}
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(e.target.value)}
          >
            {monthOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Cards de Métricas */}
      <div className="row g-4 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card-metric shadow-sm h-100">
            <div>
              <p className="card-metric-title">Receitas ({currentMonthLabel})</p>
              <h3 className="card-metric-value">
                R$ {totalCredits.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon credit">
              <FaArrowUp />
            </div>
          </div>
        </div>
        
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card-metric shadow-sm h-100">
            <div>
              <p className="card-metric-title">Despesas ({currentMonthLabel})</p>
              <h3 className="card-metric-value text-danger">
                R$ {totalDebits.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon debit">
              <FaArrowDown />
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card-metric shadow-sm h-100">
            <div>
              <p className="card-metric-title">Fundo de Caixa ({currentMonthLabel})</p>
              <h3 className={`card-metric-value ${fundoDeCaixa >= 0 ? 'text-success' : 'text-danger'}`}>
                R$ {fundoDeCaixa.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
            </div>
            <div className="card-metric-icon fund">
              <FaPiggyBank />
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card-metric shadow-sm h-100">
            <div>
              <p className="card-metric-title">
                Saldo da Conta Corrente
              </p>
              <h3 className={`card-metric-value ${(saldoAtual?.disponivel ?? 0) >= 0 ? 'text-success' : 'text-danger'}`}>
                R$ {(saldoAtual?.disponivel ?? 0).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
              {saldoAtual && (
                <div className="mt-1">
                  <span className="text-muted d-block" style={{ fontSize: '0.68rem', fontStyle: 'italic' }}>
                    Atualizado  {`${new Date(saldoAtual.createdAt).toLocaleDateString('pt-BR')}, ${new Date(saldoAtual.createdAt).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' })}`}
                  </span>
                </div>
              )}
            </div>
            <div className="card-metric-icon balance">
              <FaWallet />
            </div>
          </div>
        </div>
      </div>

      {/* Gráficos de Fluxo de Caixa e Ofensores */}
      {hasUnidade && (
        <div className="row g-4 mb-4">
          <div className="col-lg-8">
            <div className="card-content">
              <div className="card-content-header">
                <h5 className="card-content-title">Fluxo de Caixa (Débitos vs Créditos) - {currentMonthLabel}</h5>
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

          <div className="col-lg-4">
            <div className="card-content">
              <div className="card-content-header">
                <h5 className="card-content-title">Maiores Ofensores de Débito - {currentMonthLabel}</h5>
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
        </div>
      )}

      {/* Tabela de Boletos */}
      <div className="card-content">
        <div className="card-content-header">
          <div>
            <h5 className="card-content-title">
              {isAdminOrSindico() ? 'Boletos Registrados (Últimos 90 dias)' : 'Meus Boletos (Último Ano)'}
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
