package io.github.c20c01.savedData.shareData;

import io.github.c20c01.savedData.PortalPoint;

public record PortalPointInfo(String name, String describe) {
    public PortalPointInfo(PortalPoint point) {
        this(point.name(), point.describe());
    }
}
