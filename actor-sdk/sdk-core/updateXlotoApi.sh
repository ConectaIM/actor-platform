#!/bin/bash
set -e

SCRIPT_DIR=`pwd`

cd "$SCRIPT_DIR/core/core-shared-xloto/src/main/java/br/com/diegosilva/lotericas/core/api"
rm -fr "*"

cd "$SCRIPT_DIR/../sdk-api/api-codegen/"
dist/bin/api-codegen "$SCRIPT_DIR/../sdk-api/xloto.json" "$SCRIPT_DIR/core/core-shared-xloto/src/main/java/"
