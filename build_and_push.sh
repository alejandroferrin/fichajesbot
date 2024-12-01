#!/bin/sh

# Verificar si se proporcionó la versión como argumento
if [ -z "$1" ]; then
  echo "Uso: $0 <version>"
  exit 1
fi

VERSION=$1

echo "## Construyendo imagen"
docker build -t fichajesbot:$VERSION .
echo "## Cambiando tag"
docker tag fichajesbot:$VERSION alexfer/fichajesbot:$VERSION
echo "## Subiendo a docker hub"
docker push alexfer/fichajesbot:$VERSION
echo "## Proceso finalizado"
