#!/bin/bash
# PRISM Release Keystore Oluşturma
# Bir kez çalıştırın, release.keystore'u güvenli bir yerde saklayın (Git'e commit ETMEYİN).
#
# Kullanım: bash generate_release_keystore.sh

keytool -genkey -v \
  -keystore release.keystore \
  -alias prism-release \
  -keyalg RSA \
  -keysize 4096 \
  -validity 10000 \
  -storepass "$(read -rsp 'Store password: ' p; echo "$p")" \
  -keypass "$(read -rsp 'Key password: ' p; echo "$p")" \
  -dname "CN=PRISM Secure, OU=Mobile, O=PRISM Creations, L=Istanbul, S=Istanbul, C=TR"

echo ""
echo "release.keystore oluşturuldu."
echo "gradle.properties veya env var olarak signing bilgilerini ekleyin."
