package com.rosscon.llce.components.flags;

public interface FlagListener {
    void onFlagChange(Flag flag) throws FlagException;
}
