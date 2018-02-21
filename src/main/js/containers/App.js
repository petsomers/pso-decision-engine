import React from "react";
import {connect} from "react-redux";
import { Route } from 'react-router-dom';
import ResizeAware from 'react-resize-aware';
import { NavigationBar } from '../components/NavigationBar'
import { RuleSetSelection } from '../components/RuleSetSelection'
import { FileUpload }from '../components/FileUpload'

class App extends React.Component {

	constructor(props) {
		super();
		this.state={
		}
	}

	handleResize = ({ width, height }) => this.props.onWindowResize(width, height);

	openFileUpload=() => this.props.openFileUpload();

	


	render() {
		const mainScreen={position:"fixed", top:"55px", left:"310px"};
    return (
			<ResizeAware
    		style={{ position: 'absolute',top:"0px", left:"0px", height:"100%", width:"100%" }}
				onResize={this.handleResize}>
				<div>
					<NavigationBar />
					<RuleSetSelection
						restEndPoints={this.props.restEndPoints}
						versions={this.props.versions}
						layout={this.props.layout}
						openFileUpload={() => this.openFileUpload()}/>
							Hallo
					</div>
					<div style={mainScreen}>
					{this.props.mainScreen=="upload" &&
						<FileUpload />
					}
					</div>
				</ResizeAware>
	);
  }
}

const mapStateToProps = (state) => {
  return {
		layout: state.appReducer.layout,
		restEndPoints: state.appReducer.restEndPoints,
		versions: state.appReducer.versions,
		layout: state.appReducer.layout,
		mainScreen: state.appReducer.mainScreen
  };
};

const mapDispatchToProps = (dispatch) => {
	return {
		onWindowResize: (width, height) => {
			dispatch({
				type: "WINDOW_RESIZE",
				payload: {width, height}
			});
		},
		openFileUpload: (width, height) => {
			dispatch({
				type: "GOTO_UPLOAD",
				payload: {width, height}
			});
		}
	};
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
