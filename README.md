# stream-board

```bash
# run
sbt run
npm start

# test
sbt test
sbt clean coverage test coverageReport
npm test

# docker
sbt docker:publishLocal
docker run -d -p 8080:8080 --name stream-board choffmeister/stream-board:latest
```
