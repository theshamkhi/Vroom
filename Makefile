.PHONY: up down pull restart logs ps version clean

IMAGE_TAG ?= master

SET_TAG = set "IMAGE_TAG=$(IMAGE_TAG)" &&

up:
	$(SET_TAG) docker compose up -d

down:
	docker compose down

clean:
	docker compose down -v --remove-orphans

pull:
	$(SET_TAG) docker compose pull app

restart:
	$(SET_TAG) docker compose up -d --pull always --no-deps app

logs:
	docker compose logs -f --tail=200 app

ps:
	docker compose ps

version:
	@echo "Checking running version for 'app' service..."
	@docker inspect --format='{{.Config.Image}}' vroom-app
	@echo "Image ID (Hash):"
	@docker inspect --format='{{.Image}}' vroom-app
	@echo "Created At:"
	@docker inspect --format='{{.Created}}' vroom-app