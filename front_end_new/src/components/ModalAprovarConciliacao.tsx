import React from 'react';
import type { ExtratoResumoDTO } from '../types/conciliacao';

interface ModalAprovarProps {
  extrato: ExtratoResumoDTO;
  onClose: () => void;
  onConfirm: () => void;
}

const ModalAprovarConciliacao: React.FC<ModalAprovarProps> = ({ extrato, onClose, onConfirm }) => {
  return (
    <>
      <div className="modal-backdrop show"></div>
      <div className="modal d-block" tabIndex={-1}>
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content shadow">
            <div className="modal-header bg-success text-white">
              <h5 className="modal-title">Aprovar Conciliação</h5>
              <button type="button" className="btn-close btn-close-white" onClick={onClose}></button>
            </div>
            <div className="modal-body p-4 text-center">
              <p className="fs-5 mb-1">Deseja aprovar o registro abaixo?</p>
              <div className="bg-light p-3 rounded mt-3 text-start">
                <strong>Título:</strong> {extrato.tituloTransacao} <br />
                <strong>Descrição:</strong> {extrato.descricao || 'Sem descrição'}
              </div>
              <p className="mt-4 mb-0 text-muted small">
                Ao confirmar, o status deste registro será alterado para <strong>BATIDO</strong>.
              </p>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-light" onClick={onClose}>
                Cancelar
              </button>
              <button type="button" className="btn btn-success" onClick={onConfirm}>
                Confirmar
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default ModalAprovarConciliacao;
