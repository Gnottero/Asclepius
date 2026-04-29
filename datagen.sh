#!/bin/bash
PROJECT_ROOT=$(pwd)
GEN_DATA_DIR="$PROJECT_ROOT/src/main/generated/data"
GEN_ASSETS_DIR="$PROJECT_ROOT/src/main/generated/assets"
ASSETS_DIR="$PROJECT_ROOT/src/main/resources/assets"
DATA_DIR="$PROJECT_ROOT/src/main/resources/data"

echo "[Datagen] Cleaning old generated files..."
# Remove all files from resources that were previously generated
if [ -d "$GEN_DATA_DIR" ]; then
    find "$GEN_DATA_DIR" -type f | while read -r file; do
        rel="${file#$GEN_DATA_DIR/}"
        rm -f "$DATA_DIR/$rel"
    done
fi
if [ -d "$GEN_ASSETS_DIR" ]; then
    find "$GEN_ASSETS_DIR" -type f | while read -r file; do
        rel="${file#$GEN_ASSETS_DIR/}"
        rm -f "$ASSETS_DIR/$rel"
    done
fi

echo "[Datagen] Starting Fabric Data Generation..."
if ./gradlew runDatagen; then
    echo "[Datagen] Datagen successful! Moving files..."
    mkdir -p "$ASSETS_DIR" "$DATA_DIR"

    [ -d "$GEN_ASSETS_DIR" ] && cp -r "$GEN_ASSETS_DIR"/* "$ASSETS_DIR"/
    [ -d "$GEN_DATA_DIR" ] && cp -r "$GEN_DATA_DIR"/* "$DATA_DIR"/

    rm -rf "$PROJECT_ROOT/src/main/generated"
    echo "[Datagen] Done!"
else
    echo "[Datagen] Datagen failed. Please check the logs."
    exit 1
fi