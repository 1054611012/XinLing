/** Default avatar SVG (simple user silhouette) */
export const DEFAULT_AVATAR =
  'data:image/svg+xml,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100">' +
    '<rect width="100" height="100" rx="50" fill="#2d2d5e"/>' +
    '<circle cx="50" cy="38" r="18" fill="#8888aa"/>' +
    '<path d="M18 82c0-18 14-32 32-32s32 14 32 32" fill="#8888aa"/>' +
    '</svg>'
  )

/** Default cover image for audio cards */
export const DEFAULT_COVER =
  'data:image/svg+xml,' +
  encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 120 120">' +
    '<rect width="120" height="120" fill="#1a1a3e"/>' +
    '<text x="60" y="65" text-anchor="middle" fill="#7c5cff" font-size="32">🎵</text>' +
    '</svg>'
  )
