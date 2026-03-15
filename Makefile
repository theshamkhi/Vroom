.PHONY: up down pull restart logs ps

IMAGE_TAG ?= master

up:
	IMAGE_TAG=$(IMAGE_TAG) docker compose up -d

down:
	docker compose down

pull:
	IMAGE_TAG=$(IMAGE_TAG) docker compose pull app

restart:
	IMAGE_TAG=$(IMAGE_TAG) docker compose up -d --pull always --no-deps app

logs:
	docker compose logs -f --tail=200 app

ps:
	docker compose ps
