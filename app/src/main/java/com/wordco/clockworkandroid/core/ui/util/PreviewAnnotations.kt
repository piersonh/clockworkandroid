package com.wordco.clockworkandroid.core.ui.util

import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "1. 16:9", showBackground = true, device = "spec:width=360dp,height=640dp,dpi=420")
@Preview(name = "2. 37:18", showBackground = true, device = "spec:width=360dp,height=740dp,dpi=420", fontScale = 1.5f)
@Preview(name = "3. 19.5:9", showBackground = true, device = "spec:width=360dp,height=780dp,dpi=420")
@Preview(name = "4. 21:9", showBackground = true, device = "spec:width=360dp,height=840dp,dpi=420")
@Preview(name = "5. 21:9", showBackground = true,
    device = "spec:width=1179px,height=2556px,dpi=480"
)
annotation class AspectRatioPreviews