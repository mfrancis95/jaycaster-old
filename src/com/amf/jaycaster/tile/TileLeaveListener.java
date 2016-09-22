package com.amf.jaycaster.tile;

import com.amf.jaycaster.entity.Entity;
import com.amf.jaycaster.core.Game;

public interface TileLeaveListener {
    
    void onLeave(Game game, Tile tile, Entity entity);
    
}