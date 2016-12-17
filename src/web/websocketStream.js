import exponentialBackoff from './exponentialBackoff'
import hoistNonReactStatics from 'hoist-non-react-statics'
import jsonPatch from 'fast-json-patch'
import React from 'react'

function createWebsocketUrl (url) {
  const {protocol, host} = window.location
  const wsProtocol = protocol === 'https:' ? 'wss' : 'ws'

  return `${wsProtocol}://${host}${url}`
}

export default function withDocumentUpdateStream (mapPropsToUrl) {
  return function (Component) {
    class WebsocketStream extends React.Component {
      static displayName = `WebsocketStream(${Component.displayName || Component.name || 'Component'})`

      state = {
        data: null
      }

      componentDidMount () {
        if (WebSocket) {
          this.stopWebsocket = exponentialBackoff((done) => {
            const conn = new WebSocket(createWebsocketUrl(mapPropsToUrl(this.props)))
            let lastError
            let current = {}

            conn.onerror = (error) => {
              lastError = error
            }
            conn.onclose = () => {
              done(lastError)
            }
            conn.onmessage = (message) => {
              try {
                const patch = JSON.parse(message.data)
                jsonPatch.apply(current, patch)
                this.setState({data: current})
              } catch (err) {
                done(err)
                conn.close()
              }
            }

            return () => conn.close()
          }, 100, 60000)
        }
      }

      componentWillUnmount () {
        if (this.stopWebsocket) {
          this.stopWebsocket()
        }
      }

      render () {
        return <Component {...this.props} data={this.state.data}/>
      }
    }

    return hoistNonReactStatics(WebsocketStream, Component)
  }
}
