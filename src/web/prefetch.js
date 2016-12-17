import axios from 'axios'
import Bluebird from 'bluebird'
import hoistNonReactStatics from 'hoist-non-react-statics'
import React from 'react'

export default function prefetch (urls) {
  return function (Component) {
    class Prefetch extends React.Component {
      static displayName = `Prefetch(${Component.displayName || Component.name || 'Component'})`

      state = {
        loading: true
      }

      componentDidMount () {
        this.refreshIntervalId = window.setInterval(() => this.fetch(), 2000)
        this.fetch()
      }

      componentWillUnmount () {
        window.clearInterval(this.refreshIntervalId)
      }

      render () {
        const data = Object.keys(urls).reduce((d, u) => ({...d, [u]: this.state[u]}), {})
        return <Component {...this.props} {...data} loading={this.state.loading}/>
      }

      fetch () {
        const fetches = Object.keys(urls).map((key) => axios.get(urls[key]).then((res) => this.setState({[key]: res.data})))
        return Bluebird.all(fetches).finally(() => this.setState({loading: false}))
      }
    }

    return hoistNonReactStatics(Prefetch, Component)
  }
}
