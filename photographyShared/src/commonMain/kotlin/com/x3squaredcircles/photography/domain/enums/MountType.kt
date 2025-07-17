// photographyShared/src/commonMain/kotlin/com/x3squaredcircles/photography/domain/enums/MountType.kt
package com.x3squaredcircles.photography.domain.enums

enum class MountType(val value: Int) {
    // Canon
    CANON_EF(1),
    CANON_EFS(2),
    CANON_EFM(3),
    CANON_RF(4),
    CANON_FD(5),

    // Nikon
    NIKON_F(10),
    NIKON_Z(11),
    NIKON_1(12),

    // Sony
    SONY_E(20),
    SONY_FE(21),
    SONY_A(22),

    // Fujifilm
    FUJIFILM_X(30),
    FUJIFILM_GFX(31),

    // Pentax
    PENTAX_K(40),
    PENTAX_Q(41),

    // Micro Four Thirds
    MICRO_FOUR_THIRDS(50),

    // Leica
    LEICA_M(60),
    LEICA_L(61),
    LEICA_SL(62),
    LEICA_TL(63),

    // Olympus
    OLYMPUS_FOUR_THIRDS(70),

    // Panasonic
    PANASONIC_L(80),

    // Sigma
    SIGMA_SA(90),

    // Tamron
    TAMRON_ADAPTALL(100),

    // Generic/Other
    C(200),
    CS(201),
    M42(202),
    T2(203),
    OTHER(999);

    companion object {
        fun fromValue(value: Int): MountType {
            return values().find { it.value == value } ?: OTHER
        }
    }
}