import styles from './ProgressBar.scss'
import React, {PropTypes} from 'react'

export default class ProgressBar extends React.Component {
  static propTypes = {
    position: PropTypes.number.isRequired,
    length: PropTypes.number
  }

  render () {
    const {position, length} = this.props
    return (
      <div className={styles.container}>
        <div className={styles.fill} style={{width: length ? `${position / length * 100}%` : '0%'}}/>
      </div>
    )
  }
}
