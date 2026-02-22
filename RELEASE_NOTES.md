## Prepare release v1.32.12 BETA: version bump and Android release CI

Workflows failed at parse time with `if: ${{ secrets.KEYSTORE_BASE64 != '' }}`. Secrets context unavailable in workflow-level conditionals.

## Changes

- Remove `if: ${{ secrets.KEYSTORE_BASE64 != '' }}` from workflow steps
- Move conditional logic into shell script: `if [ -n "$KEYSTORE_BASE64" ]; then`
- Apply to both `android-release-build.yml` and `build-release.yml`

**Before:**
```yaml
- name: Decode keystore
  if: ${{ secrets.KEYSTORE_BASE64 != '' }}
  env:
    KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}
  run: |
    echo "$KEYSTORE_BASE64" | base64 --decode > android/app/release.keystore
```

**After:**
```yaml
- name: Decode keystore
  env:
    KEYSTORE_BASE64: ${{ secrets.KEYSTORE_BASE64 }}
  run: |
    if [ -n "$KEYSTORE_BASE64" ]; then
      echo "$KEYSTORE_BASE64" | base64 --decode > android/app/release.keystore
    fi
```

(Use PR #4 body as release notes).