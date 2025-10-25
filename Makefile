.PHONY: build up down restart logs clean rebuild test package help

help:
	@echo "Comandos disponibles:"
	@echo "  make build      - Construir las imágenes Docker"
	@echo "  make up         - Levantar los contenedores"
	@echo "  make down       - Detener los contenedores"
	@echo "  make restart    - Reiniciar los contenedores"
	@echo "  make logs       - Ver logs de todos los contenedores"
	@echo "  make logs-app   - Ver logs de la aplicación"
	@echo "  make logs-db    - Ver logs de la base de datos"
	@echo "  make clean      - Detener y eliminar contenedores, redes y volúmenes"
	@echo "  make rebuild    - Limpiar y reconstruir todo desde cero"
	@echo "  make test       - Ejecutar tests con Maven"
	@echo "  make package    - Empaquetar la aplicación con Maven"
	@echo "  make db-shell   - Acceder al shell de MySQL"
	@echo "  make app-shell  - Acceder al shell del contenedor de la app"

build:
	docker compose build --no-cache

up:
	docker compose up -d

down:
	docker compose down

restart:
	docker compose restart

logs:
	docker compose logs -f

logs-app:
	docker compose logs -f app

logs-db:
	docker compose logs -f mysql

clean:
	docker compose down -v --remove-orphans
	docker system prune -f

rebuild: clean build up

test:
	mvn clean test

package:
	mvn clean package -DskipTests

db-shell:
	docker exec -it mysql mysql -u user -ppassword db

app-shell:
	docker compose exec app bash

status:
	docker compose ps

