package ir.parscode.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * PARS CODE visual identity: deep black + metallic gold, luxury/minimal.
 * Gold is reserved for accents, progress, and primary actions - never used
 * as a large fill, matching the reference screens (mostly-black cards with
 * thin gold borders/glow, gold used sparingly for emphasis).
 */

// Backgrounds
val PcBackground = Color(0xFF07070A)      // app background, near-black
val PcSurface = Color(0xFF121014)         // card surface, one step up from bg
val PcSurfaceRaised = Color(0xFF1A1710)   // raised/selected surface, warm-black

// Gold family
val PcGold = Color(0xFFD4AF37)            // primary gold - main accent
val PcGoldBright = Color(0xFFF5D485)      // highlight edge of gradients/glow
val PcGoldDim = Color(0xFF8B6914)         // deep gold, gradient tail
val PcGoldMuted = Color(0xFFB8935A)       // secondary text on dark

// Borders / lines
val PcBorder = Color(0xFF3A2F1A)          // subtle gold-tinted border
val PcBorderBright = Color(0x66D4AF37)    // brighter border for active/glow state

// Text
val PcTextPrimary = Color(0xFFF5F0E6)     // warm off-white
val PcTextSecondary = Color(0xFFB8AFA0)   // muted warm gray
val PcTextDisabled = Color(0xFF5C574C)

// Status
val PcSuccess = Color(0xFF5FBE7A)
val PcWarning = Color(0xFFE0A94E)
val PcDanger = Color(0xFFE0665F)
