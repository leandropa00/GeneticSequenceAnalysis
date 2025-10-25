.PHONY: build run down

build:
	@echo "Building application..."
	@docker compose build --no-cache

run:
	@echo "Starting database..."
	@docker compose run --rm app

down:
	@echo "Stopping application..."
	@docker compose down