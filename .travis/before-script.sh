
#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_ac5e8b237191_key -iv $encrypted_ac5e8b237191_iv -in .travis/codesigning.asc.enc -out .travis/codesigning.asc -d

    gpg --fast-import .travis/codesigning.asc
fi
