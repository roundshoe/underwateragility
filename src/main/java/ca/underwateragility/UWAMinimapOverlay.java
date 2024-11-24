/*
 * Copyright (c) 2024, roundshoe <https://github.com/roundshoe>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ca.underwateragility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Collection;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
class UWAMinimapOverlay extends Overlay
{
	private final UWAPlugin plugin;
	private final UWAConfig config;

	@Inject
	UWAMinimapOverlay(final UWAPlugin plugin, final UWAConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(Overlay.PRIORITY_HIGH);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		var style = config.bubbleOverlay();

		if (style == UWAConfig.OverlayStyle.MINIMAP || style == UWAConfig.OverlayStyle.BOTH)
		{
			renderMinimapTileObject(graphics2D, plugin.getBubbles(), config.bubbleOutlineColor());
		}

		style = config.holeOverlay();

		if (style == UWAConfig.OverlayStyle.MINIMAP || style == UWAConfig.OverlayStyle.BOTH)
		{
			renderMinimapTileObject(graphics2D, plugin.getHoles(), config.holeOutlineColor());
		}

		style = config.chestClamOverlay();

		if (style == UWAConfig.OverlayStyle.MINIMAP || style == UWAConfig.OverlayStyle.BOTH)
		{
			renderMinimapTileObject(graphics2D, plugin.getChestClams(), config.chestClamOutlineColor());
		}

		style = config.pufferFishOverlay();

		if (style == UWAConfig.OverlayStyle.MINIMAP || style == UWAConfig.OverlayStyle.BOTH)
		{
			renderMinimapActor(graphics2D, plugin.getPufferFish(), config.pufferFishOutlineColor());
		}

		return null;
	}

	private void renderMinimapTileObject(final Graphics2D graphics2D, final Collection<TileObject> tileObjects, final Color color)
	{
		tileObjects.stream()
			.map(TileObject::getMinimapLocation)
			.filter(Objects::nonNull)
			.forEach(p -> OverlayUtil.renderMinimapLocation(graphics2D, p, color));
	}

	private void renderMinimapActor(final Graphics2D graphics2D, final Collection<NPC> npcs, final Color color)
	{
		npcs.stream()
			.map(Actor::getMinimapLocation)
			.filter(Objects::nonNull)
			.forEach(p -> OverlayUtil.renderMinimapLocation(graphics2D, p, color));
	}
}
