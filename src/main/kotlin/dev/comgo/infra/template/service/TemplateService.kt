package dev.comgo.infra.template.service

import dev.comgo.infra.cache.service.CacheService
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.util.HtmlUtils
import java.time.Duration

@Service
class TemplateService(
    private val cacheService: CacheService
) {
    suspend fun getTemplate(name: String): String {
        val cacheKey = "template:$name"
        return cacheService.get(cacheKey) ?: run {
            val resource = ClassPathResource("templates/$name.html")
            val template = resource.inputStream.bufferedReader().use { it.readText() }
            cacheService.set(cacheKey, template, Duration.ofHours(24))
            template
        }
    }

    fun renderTemplate(template: String, variables: Map<String, Any>): String {
        var result = template
        variables.forEach { (key, value) ->
            HtmlUtils.htmlEscape(value.toString()).let {
                result = result.replace("{{$key}}", it)
            }
        }
        return result
    }
}