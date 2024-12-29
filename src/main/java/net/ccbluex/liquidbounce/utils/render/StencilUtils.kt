package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.utils.client.MinecraftInstance
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.EXTFramebufferObject
import org.lwjgl.opengl.GL11.*


object StencilUtils : MinecraftInstance {
    fun dispose() {
        glDisable(GL_STENCIL_TEST)
        GlStateManager.disableAlpha()
        GlStateManager.disableBlend()
    }

    fun erase(invert: Boolean) {
        glStencilFunc(if (invert) GL_EQUAL else GL_NOTEQUAL, 1, 65535)
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
        GlStateManager.colorMask(true, true, true, true)
        GlStateManager.enableAlpha()
        GlStateManager.enableBlend()
        glAlphaFunc(GL_GREATER, 0.0f)
    }

    fun write(renderClipLayer: Boolean) {
        checkSetupFBO()
        glClearStencil(0)
        glClear(GL_STENCIL_BUFFER_BIT)
        glEnable(GL_STENCIL_TEST)
        glStencilFunc(GL_ALWAYS, 1, 65535)
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
        if (!renderClipLayer) GlStateManager.colorMask(false, false, false, false)
    }

    fun write(renderClipLayer: Boolean, fb: Framebuffer?, clearStencil: Boolean, invert: Boolean) {
        checkSetupFBO(fb)
        if (clearStencil) {
            glClearStencil(0)
            glClear(GL_STENCIL_BUFFER_BIT)
            glEnable(GL_STENCIL_TEST)
        }
        glStencilFunc(GL_ALWAYS, if (invert) 0 else 1, 65535)
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
        if (!renderClipLayer) GlStateManager.colorMask(false, false, false, false)
    }

    fun checkSetupFBO() {
        val fbo: Framebuffer = mc.framebuffer
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo)
            fbo.depthBuffer = -1
        }
    }

    fun checkSetupFBO(fbo: Framebuffer?) {
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo)
            fbo.depthBuffer = -1
        }
    }

    fun setupFBO(fbo: Framebuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer)
        val stencilDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT()
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencilDepthBufferId)
        EXTFramebufferObject.glRenderbufferStorageEXT(
            36161,
            34041,
            Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight
        )
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencilDepthBufferId)
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencilDepthBufferId)
    }
}