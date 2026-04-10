#!/bin/bash
PROJECT_ROOT=$(pwd)
GEN_DATA_DIR="$PROJECT_ROOT/src/main/generated/data"
GEN_ASSETS_DIR="$PROJECT_ROOT/src/main/generated/assets"
ASSETS_DIR="$PROJECT_ROOT/src/main/resources/assets"
DATA_DIR="$PROJECT_ROOT/src/main/resources/data"

echo "[Datagen] Starting Fabric Data Generation..."
if ./gradlew runDatagen; then
    echo "[Datagen] Datagen successful! Moving files..."
    mkdir -p "$ASSETS_DIR" "$DATA_DIR"
    cp -r "$GEN_ASSETS_DIR"/* "$ASSETS_DIR"/ 2>/dev/null || true
    cp -r "$GEN_DATA_DIR"/* "$DATA_DIR"/ 2>/dev/null || true
    rm -rf "$PROJECT_ROOT/src/main/generated"
else
    echo "[Datagen] Datagen failed. Please check the logs."
    exit 1
fi