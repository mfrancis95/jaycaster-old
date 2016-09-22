package com.amf.jaycaster.tile;

import com.amf.jaycaster.entity.Entity;
import com.amf.jaycaster.core.Game;

public interface TileEnterListener {
    
    void onEnter(Game game, Tile tile, Entity entity);
    
} 