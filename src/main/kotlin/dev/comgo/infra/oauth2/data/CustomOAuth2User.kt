package dev.comgo.infra.oauth2.data

import dev.comgo.domain.user.domain.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val oauth2User: OAuth2User,
    val user: UserEntity
) : OAuth2User {
    override fun getName(): String {
        return user.id.toString()
    }

    override fun getAttributes(): Map<String, Any> {
        return oauth2User.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_" + user.role))
    }
}