.PHONY: build
build:
	./mvnw package

.PHONY: test
test:
	./mvnw test

.PHONY: clean
clean:
	./mvnw clean

