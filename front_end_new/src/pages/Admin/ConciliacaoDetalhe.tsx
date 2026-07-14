import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FaArrowLeft, FaSpinner, FaCheck, FaEdit, FaDownload, FaFileAlt, FaFilePdf } from 'react-icons/fa';
import { getExtratosPaginado, atualizarExtrato, uploadComprovanteLote, getComprovanteDownloadUrl, uploadComprovanteIndividual, gerarPdf, baixarPdf } from '../../services/conciliacaoService';
import { getCategoriasAtivas } from '../../services/categoriaService';
import type { ExtratoResumoDTO, ExtratoConciliacaoPatchDTO, Page } from '../../types/conciliacao';
import type { CategoriaGasto } from '../../types/categoria';
import ModalAprovarConciliacao from '../../components/ModalAprovarConciliacao';
import ModalEditarExtrato from '../../components/ModalEditarExtrato';
import { useAuth } from '../../context/AuthContext';

const ConciliacaoDetalhe: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAdminOrSindico } = useAuth();

  const [extratosPage, setExtratosPage] = useState<Page<ExtratoResumoDTO> | null>(null);
  const [categorias, setCategorias] = useState<CategoriaGasto[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [currentPage, setCurrentPage] = useState<number>(0);
  
  // Modals state
  const [selectedExtrato, setSelectedExtrato] = useState<ExtratoResumoDTO | null>(null);
  const [showModalAprovar, setShowModalAprovar] = useState<boolean>(false);
  const [showModalEditar, setShowModalEditar] = useState<boolean>(false);
  const [uploadingLote, setUploadingLote] = useState<boolean>(false);
  const [generatingPdf, setGeneratingPdf] = useState<boolean>(false);

  useEffect(() => {
    carregarCategorias();
  }, []);

  useEffect(() => {
    if (id) {
      carregarExtratos(Number(id), currentPage);
    }
  }, [id, currentPage]);

  const carregarCategorias = async (): Promise<void> => {
    try {
      const data = await getCategoriasAtivas();
      setCategorias(data);
    } catch (error) {
      console.error('Erro ao carregar categorias', error);
    }
  };

  const carregarExtratos = async (conciliacaoId: number, page: number): Promise<void> => {
    try {
      setLoading(true);
      const data = await getExtratosPaginado(conciliacaoId, page, 20);
      setExtratosPage(data);
    } catch (error) {
      console.error('Erro ao carregar extratos', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage: number): void => {
    if (extratosPage && newPage >= 0 && newPage < extratosPage.totalPages) {
      setCurrentPage(newPage);
    }
  };

  const openAprovarModal = (extrato: ExtratoResumoDTO): void => {
    setSelectedExtrato(extrato);
    setShowModalAprovar(true);
  };

  const openEditarModal = (extrato: ExtratoResumoDTO): void => {
    setSelectedExtrato(extrato);
    setShowModalEditar(true);
  };

  const onConfirmAprovar = async (): Promise<void> => {
    if (!selectedExtrato) return;
    try {
      const dto: ExtratoConciliacaoPatchDTO = {
        statusConciliado: 'BATIDO',
      };
      await atualizarExtrato(selectedExtrato.id, dto);
      setShowModalAprovar(false);
      carregarExtratos(Number(id), currentPage);
    } catch (error) {
      console.error('Erro ao aprovar', error);
      alert('Erro ao aprovar extrato.');
    }
  };

  const onConfirmEditar = async (dto: ExtratoConciliacaoPatchDTO, file?: File): Promise<void> => {
    if (!selectedExtrato) return;
    try {
      await atualizarExtrato(selectedExtrato.id, dto);
      if (file) {
        await uploadComprovanteIndividual(selectedExtrato.id, file);
      }
      setShowModalEditar(false);
      carregarExtratos(Number(id), currentPage);
    } catch (error) {
      console.error('Erro ao editar', error);
      alert('Erro ao editar extrato ou anexar comprovante.');
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  const statusBadge = (status: string) => {
    if (status === 'BATIDO') return <span className="badge bg-success">Batido</span>;
    return <span className="badge bg-warning text-dark">Pendente</span>;
  };

  const formatData = (data: string | number[] | null | undefined) => {
    if (!data) return '-';
    if (Array.isArray(data)) {
      return `${String(data[2]).padStart(2, '0')}/${String(data[1]).padStart(2, '0')}/${data[0]}`;
    }
    const d = String(data);
    if (d.includes('-')) {
      const parts = d.split('T')[0].split('-');
      if (parts.length >= 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    }
    return new Date(d).toLocaleDateString('pt-BR');
  };

  const handleUploadLote = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const file = e.target.files[0];
      try {
        setUploadingLote(true);
        await uploadComprovanteLote(Number(id), file);
        alert('Comprovante em lote anexado com sucesso!');
        carregarExtratos(Number(id), currentPage);
      } catch (error) {
        console.error('Erro ao anexar comprovante em lote', error);
        alert('Erro ao anexar comprovante em lote.');
      } finally {
        setUploadingLote(false);
      }
    }
  };

  const handleGerarPdf = async () => {
    try {
      setGeneratingPdf(true);
      await gerarPdf(Number(id));
      alert('PDF gerado com sucesso! Iniciando download...');
      await baixarPdf(Number(id));
    } catch (error) {
      console.error('Erro ao gerar PDF', error);
      alert('Erro ao gerar PDF. Tente novamente mais tarde.');
    } finally {
      setGeneratingPdf(false);
    }
  };

  const handleBaixarPdf = async () => {
    try {
      await baixarPdf(Number(id));
    } catch (error) {
      console.error('Erro ao baixar PDF', error);
      alert('PDF ainda não foi gerado para esta conciliação.');
    }
  };

  return (
    <div>
      <div className="d-flex align-items-center justify-content-between mb-4">
        <div className="d-flex align-items-center gap-3">
          <button className="btn btn-outline-secondary rounded-circle" onClick={() => navigate(-1)} title="Voltar">
            <FaArrowLeft />
          </button>
          <div>
            <h2 className="mb-0 d-flex align-items-center gap-2">
              <FaFileAlt className="text-primary" />
              Detalhes da Conciliação
            </h2>
            <p className="text-muted small mb-0">Listagem de lançamentos (Extrato).</p>
          </div>
        </div>
        <div className="d-flex gap-2">
          {isAdminOrSindico() && (
            <div className="btn-group">
              <button 
                className="btn btn-outline-danger d-flex align-items-center gap-2"
                onClick={handleGerarPdf}
                disabled={generatingPdf}
                title="Gerar e sobrescrever PDF atual"
              >
                {generatingPdf ? <FaSpinner className="fa-spin" /> : <FaFilePdf />}
                Gerar PDF
              </button>
              <button 
                className="btn btn-outline-danger d-flex align-items-center"
                onClick={handleBaixarPdf}
                title="Baixar PDF salvo"
              >
                <FaDownload />
              </button>
            </div>
          )}
          <label className="btn btn-outline-primary mb-0" style={{ cursor: 'pointer' }}>
            {uploadingLote ? (
              <><FaSpinner className="fa-spin me-2" /> Anexando...</>
            ) : (
              <>Anexar Comprovante do Mês</>
            )}
            <input type="file" style={{ display: 'none' }} onChange={handleUploadLote} disabled={uploadingLote} />
          </label>
        </div>
      </div>

      <div className="card shadow-sm border-0">
        <div className="card-body p-0">
          {loading ? (
            <div className="p-5 text-center">
              <FaSpinner className="fa-spin text-primary fs-3" />
              <p className="mt-2 text-muted">Carregando extratos...</p>
            </div>
          ) : !extratosPage || extratosPage.content.length === 0 ? (
            <div className="p-5 text-center text-muted">
              Nenhum extrato encontrado nesta conciliação.
            </div>
          ) : (
            <>
              <div className="table-responsive">
                <table className="table table-hover align-middle mb-0" style={{ fontSize: '0.9rem' }}>
                  <thead className="table-light">
                    <tr>
                      <th>Data</th>
                      <th>Título Transação</th>
                      <th>Tipo</th>
                      <th>Valor</th>
                      <th>Descrição</th>
                      <th>Categoria</th>
                      <th className="text-center">Status</th>
                      <th className="text-center">Comprovante</th>
                      <th className="text-end">Ações</th>
                    </tr>
                  </thead>
                  <tbody>
                    {extratosPage.content.map((e) => (
                      <tr key={e.id}>
                        <td>{formatData(e.dtTransacao as any)}</td>
                        <td>{e.tituloTransacao}</td>
                        <td>
                          <span className={`badge ${e.tipoOperacao === 'DEBITO' || e.tipoOperacao === 'D' ? 'bg-danger' : 'bg-success'}`}>
                            {e.tipoOperacao}
                          </span>
                        </td>
                        <td className="fw-bold">{formatCurrency(e.valorTransacao)}</td>
                        <td>{e.descricao || '-'}</td>
                        <td>{e.descricaoCategoriaGasto || '-'}</td>
                        <td className="text-center">{statusBadge(e.statusConciliado)}</td>
                        <td className="text-center">
                          {e.possuiComprovante && e.idComprovante ? (
                            <a href={getComprovanteDownloadUrl(e.idComprovante)} target="_blank" rel="noreferrer" className="btn btn-sm btn-outline-primary" title={e.nomeArquivoComprovante || 'Download'}>
                              <FaDownload />
                            </a>
                          ) : (
                            <span className="text-muted">-</span>
                          )}
                        </td>
                        <td className="text-end">
                          <button
                            className="btn btn-sm btn-light text-primary me-2"
                            onClick={() => openEditarModal(e)}
                            title="Editar"
                          >
                            <FaEdit />
                          </button>
                          <button
                            className="btn btn-sm btn-light text-success"
                            onClick={() => openAprovarModal(e)}
                            title="Aprovar"
                          >
                            <FaCheck />
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              
              {/* Paginação */}
              <div className="d-flex justify-content-between align-items-center p-3 border-top">
                <span className="text-muted small">
                  Mostrando página {extratosPage.number + 1} de {extratosPage.totalPages} ({extratosPage.totalElements} registros)
                </span>
                <nav>
                  <ul className="pagination pagination-sm mb-0">
                    <li className={`page-item ${extratosPage.first ? 'disabled' : ''}`}>
                      <button className="page-link" onClick={() => handlePageChange(extratosPage.number - 1)}>
                        Anterior
                      </button>
                    </li>
                    <li className={`page-item ${extratosPage.last ? 'disabled' : ''}`}>
                      <button className="page-link" onClick={() => handlePageChange(extratosPage.number + 1)}>
                        Próxima
                      </button>
                    </li>
                  </ul>
                </nav>
              </div>
            </>
          )}
        </div>
      </div>

      {showModalAprovar && selectedExtrato && (
        <ModalAprovarConciliacao
          extrato={selectedExtrato}
          onClose={() => setShowModalAprovar(false)}
          onConfirm={onConfirmAprovar}
        />
      )}

      {showModalEditar && selectedExtrato && (
        <ModalEditarExtrato
          extrato={selectedExtrato}
          categorias={categorias}
          onClose={() => setShowModalEditar(false)}
          onConfirm={onConfirmEditar}
        />
      )}
    </div>
  );
};

export default ConciliacaoDetalhe;
