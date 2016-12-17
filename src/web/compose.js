export default (...fns) => fns.reverse().reduce((acc, fn) => x => fn(acc(x)), x => x)
