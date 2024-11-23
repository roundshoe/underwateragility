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

import ca.underwateragility.tools.WorldLines;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@Singleton
class UWALinesOverlay extends Overlay
{
	private static final List<List<WorldPoint>> HOLE_WORLD_POINTS = List.of(
		List.of(new WorldPoint(3772, 10267, 1), new WorldPoint(3823, 10247, 1)),
		List.of(new WorldPoint(3772, 10280, 1), new WorldPoint(3816, 10271, 1)),
		List.of(new WorldPoint(3789, 10298, 1), new WorldPoint(3833, 10289, 1)),
		List.of(new WorldPoint(3754, 10241, 1), new WorldPoint(3779, 10241, 1)),
		List.of(new WorldPoint(3779, 10277, 1), new WorldPoint(3716, 10243, 1))
	);

	private final Client client;
	private final UWAPlugin plugin;
	private final UWAConfig config;

	@Inject
	UWALinesOverlay(
		final Client client,
		final UWAPlugin plugin,
		final UWAConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(Overlay.PRIORITY_LOW);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (config.holeLines() && (!config.holeLinesPressKey() || plugin.isKeyPressed()))
		{
			for (final var points : HOLE_WORLD_POINTS)
			{
				WorldLines.drawLinesOnWorld(graphics2D, client, points, Color.CYAN);
			}
		}

		return null;
	}
}
