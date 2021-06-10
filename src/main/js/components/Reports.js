import React, { Component } from 'react'

import Report from 'components/Report'

const Reports = (props) => {
    if (props.loading) {
        return null
    }
    if (props.reports.length > 0) {
        const reports = props.reports.map((reportModel) => (
            <Report key={reportModel.id} report={reportModel} openModalMethod={props.openModalMethod}/>
        ))

      return (
        <ol className="reportData">
            {reports}
        </ol>
        );
    } else {
      return <div className="rvt-m-top-lg">No available reports</div>;
    }
  }

export default Reports
