MAKEFLAGS += --silent

.PHONY: build
build:
	./mvnw package

.PHONY: test
test:
	./mvnw test

.PHONY: clean
clean:
	./mvnw clean

.PHONY: docker-build
docker-build:
	./docker-build.sh

.PHONY: docker-run
docker-run:
	./docker-run.sh

.PHONY: rebalance
rebalance:
	./bin/join_rebalance_request.sh /tmp/portfolio.json tmp/ideal.json > /tmp/rebalance_request.json
	curl -s localhost:8081/rebalance -XPOST -H "Content-Type: application/json" --data-binary @/tmp/rebalance_request.json > /tmp/rebalance_orders.json

