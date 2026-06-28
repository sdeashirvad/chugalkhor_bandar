package com.chugalkhorbandar.adapters.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "character_credentials")
public class CharacterCredentialEntity {

    @Id
    @Column(name = "character_id")
    private String characterId;

    private String passkey;

    public CharacterCredentialEntity() {}

    public CharacterCredentialEntity(String characterId, String passkey) {
        this.characterId = characterId;
        this.passkey = passkey;
    }

    public String getCharacterId() {
        return characterId;
    }

    public void setCharacterId(String characterId) {
        this.characterId = characterId;
    }

    public String getPasskey() {
        return passkey;
    }

    public void setPasskey(String passkey) {
        this.passkey = passkey;
    }
}
