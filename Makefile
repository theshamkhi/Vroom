.PHONY: up down pull restart logs ps version

IMAGE_TAG ?= master

SET_TAG = set "IMAGE_TAG=$(IMAGE_TAG)" &&

up:
	$(SET_TAG) docker compose up -d

down:
	docker compose down

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
	@docker inspect --format='{{.Config.Image}}' vroom-app-1
	@echo "Image ID (Hash):"
	@docker inspect --format='{{.Image}}' vroom-app-1
	@echo "Created At:"
	@docker inspect --format='{{.Created}}' vroom-app-1
