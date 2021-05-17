MAKEFLAGS += --silent

install:
	$(MAKE) install_githooks
.PHONY: install

install_githooks:
	chmod +x ./githooks/*
	cp ./githooks/* .git/hooks/
.PHONY: install_githooks


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


