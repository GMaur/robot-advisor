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

