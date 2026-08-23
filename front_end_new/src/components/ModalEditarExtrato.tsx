import React, { useState } from 'react';
import type { ExtratoResumoDTO, ExtratoConciliacaoPatchDTO, StatusConciliacao } from '../types/conciliacao';
import type { CategoriaGasto } from '../types/categoria';

interface ModalEditarProps {
  extrato: ExtratoResumoDTO;
  categorias: CategoriaGasto[];
  onClose: () => void;
  onConfirm: (dto: ExtratoConciliacaoPatchDTO, file?: File) => void;
}

const ModalEditarExtrato: React.FC<ModalEditarProps> = ({ extrato, categorias, onClose, onConfirm }) => {
  const [idCategoriaGasto, setIdCategoriaGasto] = useState<number | ''>(extrato.idCategoriaGasto || '');
  const [statusConciliado, setStatusConciliado] = useState<StatusConciliacao>(extrato.statusConciliado);
  const [fileToUpload, setFileToUpload] = useState<File | undefined>();
  const [errorMsg, setErrorMsg] = useState<string>('');

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setFileToUpload(file);
    } else {
      setFileToUpload(undefined);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validação da regra: Só pode aprovar (BATIDO) Débito se tiver descrição
    const descText = (extrato.descricao || '').trim();
    if (extrato.tipoOperacao === 'DEBITO' && statusConciliado === 'BATIDO' && !descText) {
      setErrorMsg('Para aprovar um registro de DÉBITO, a descrição é obrigatória.');
      return;
    }

    const dto: ExtratoConciliacaoPatchDTO = {
      descricao: descText || undefined,
      idCategoriaGasto: idCategoriaGasto !== '' ? Number(idCategoriaGasto) : undefined,
      statusConciliado: statusConciliado,
    };

    onConfirm(dto, fileToUpload);
  };

  return (
    <>
      <div className="modal-backdrop show"></div>
      <div className="modal d-block" tabIndex={-1}>
        <div className="modal-dialog modal-lg modal-dialog-centered">
          <form className="modal-content shadow" onSubmit={handleSubmit}>
            <div className="modal-header bg-primary text-white">
              <h5 className="modal-title">Editar Extrato</h5>
              <button type="button" className="btn-close btn-close-white" onClick={onClose}></button>
            </div>
            
            <div className="modal-body p-4" style={{ minHeight: '400px' }}>
              {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}
              
              <div className="row bg-light p-3 rounded mb-4 mx-0">
                <div className="col-md-4">
                  <label className="text-muted small">Título Transação</label>
                  <div className="fw-bold">{extrato.tituloTransacao}</div>
                </div>
                <div className="col-md-4">
                  <label className="text-muted small">Tipo de Operação</label>
                  <div>
                    <span className={`badge ${extrato.tipoOperacao === 'DEBITO' ? 'bg-danger' : 'bg-success'}`}>
                      {extrato.tipoOperacao}
                    </span>
                  </div>
                </div>
                <div className="col-md-4">
                  <label className="text-muted small">Valor</label>
                  <div className="fw-bold">{formatCurrency(extrato.valorTransacao)}</div>
                </div>
              </div>

              <div className="row g-3">
                <div className="col-md-12">
                  <label className="form-label">Descrição</label>
                  <input
                    type="text"
                    className="form-control bg-light"
                    value={extrato.descricao || ''}
                    disabled
                    placeholder="Descrição do lançamento"
                  />
                </div>
                
                <div className="col-md-6">
                  <label className="form-label">Categoria de Gasto</label>
                  <select
                    className="form-select"
                    value={idCategoriaGasto}
                    onChange={(e) => setIdCategoriaGasto(e.target.value === '' ? '' : Number(e.target.value))}
                  >
                    <option value="">Selecione...</option>
                    {categorias.map((cat) => (
                      <option key={cat.id} value={cat.id}>
                        {cat.descricao}
                      </option>
                    ))}
                  </select>
                </div>
                
                <div className="col-md-6">
                  <label className="form-label">Status Conciliação</label>
                  <select
                    className="form-select"
                    value={statusConciliado}
                    onChange={(e) => setStatusConciliado(e.target.value as StatusConciliacao)}
                  >
                    <option value="PENDENTE">Pendente</option>
                    <option value="BATIDO">Batido</option>
                  </select>
                </div>

                <div className="col-md-12">
                  <label className="form-label">Comprovante (Imagem ou PDF)</label>
                  <input
                    type="file"
                    className="form-control"
                    accept="image/*,application/pdf"
                    onChange={handleFileChange}
                  />
                </div>
              </div>
            </div>
            
            <div className="modal-footer">
              <button type="button" className="btn btn-light" onClick={onClose}>
                Cancelar
              </button>
              <button type="submit" className="btn btn-primary">
                Salvar
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default ModalEditarExtrato;
