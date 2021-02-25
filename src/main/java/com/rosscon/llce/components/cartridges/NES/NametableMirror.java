package com.rosscon.llce.components.cartridges.NES;

/**
 * Wrapper for how nametable mirroring is handles. This is to allow for extension
 * in more complex mappers where mirroring can change on the fly.
 */
public class NametableMirror {

    private NESNametableMirroring mirrorMode;

    public NametableMirror(){
        this.mirrorMode = NESNametableMirroring.VERTICAL;
    }

    public NametableMirror(NESNametableMirroring mirrorMode){
        this.mirrorMode = mirrorMode;
    }

    public NESNametableMirroring getMirrorMode() {
        return mirrorMode;
    }
}
