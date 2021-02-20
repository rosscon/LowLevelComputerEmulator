package com.rosscon.llce.components.flags;

import com.rosscon.llce.components.memory.MemoryException;

public interface FlagListener {
    void onFlagChange(boolean newValue, Flag flag) throws MemoryException;
}
