import React from 'react'

const Report = (props) => {
  var reportModel = props.report;

  var kbLink = '';

  if (reportModel.kbUrl != null) {
      kbLink = <a href={reportModel.kbUrl} target='_blank'>Learn More</a>;
  }

  var reportTarget = '';
  if (reportModel.newWindow) {
    reportTarget = "_blank";
  }

  return (
        <li className="dataView">
            <div className="rvt-box rvt-box--card boxCardOverride">
                <div className="rvt-box__image">
                    <img src={reportModel.thumbnail} alt="Report Thumbnail" data-modal-trigger="modal-card-popup"
                            onClick={openModal.bind(this, reportModel.id, props.openModalMethod)} className="thumbnail"/>
                </div>
                <div className="rvt-box__body">
                    <h2 className="rvt-ts-18 rvt-text-bold">{reportModel.title}</h2>
                    <p className="rvt-m-top-xxs">{reportModel.description} {kbLink}</p>
                    <a href={reportModel.url} className="rvt-link-bold" target={reportTarget}>Open Report</a>
                </div>
            </div>
        </li>
      );
}

function openModal(reportId, openModalMethod, event) {
    openModalMethod(reportId)
}

export default Report
