import React from 'react'

const ReportModal = (props) => {
    let modalContents;

    if (props.modalReport) {
        modalContents = (
            <div className="rvt-modal__inner">
                <header className="rvt-modal__header">
                    <h1 className="rvt-modal__title" id="modal-title">Preview of {props.modalReport.title}</h1>
                </header>
                <div className="rvt-modal__body">
                    <img src={props.modalReport.image} alt={props.modalReport.title + ' preview'} className="modalImageWidth" />
                </div>
                <div className="rvt-modal__controls">
                    <button type="button" className="rvt-button" data-modal-close="close">Close</button>
                </div>
                <button type="button" className="rvt-button rvt-modal__close" data-modal-close="close">
                    <span className="rvt-sr-only">Close</span>
                    <svg aria-hidden="true" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 16 16">
                        <path fill="currentColor" d="M9.41,8l5.29-5.29a1,1,0,0,0-1.41-1.41L8,6.59,2.71,1.29A1,1,0,0,0,1.29,2.71L6.59,8,1.29,13.29a1,1,0,1,0,1.41,1.41L8,9.41l5.29,5.29a1,1,0,0,0,1.41-1.41Z"/>
                    </svg>
                </button>
            </div>
        )
    }

    return (
        <div className="rvt-modal"
             id="modal-card-popup"
             role="dialog"
             aria-labelledby="modal-title"
             aria-hidden="true"
             tabIndex="-1">
                {modalContents}
            </div>
    )
}

export default ReportModal