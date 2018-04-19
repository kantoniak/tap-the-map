#!/bin/bash
# Renames country_CODE.obj to country_CODE_obj and does the same for mtl files

for file in country_*.obj; do
    mv "$file" "$(basename "$file" .obj)_obj"
done

for file in country_*.mtl; do
    mv "$file" "$(basename "$file" .mtl)_mtl"
done
