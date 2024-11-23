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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.client.util.ColorUtil;

@Singleton
class UWAOverlay extends Overlay
{
	private static final Stroke STROKE = new BasicStroke(1);

	private final Client client;
	private final UWAPlugin plugin;
	private final UWAConfig config;
	private final ModelOutlineRenderer modelOutlineRenderer;

	@Inject
	UWAOverlay(
		final Client client,
		final UWAPlugin plugin,
		final UWAConfig config,
		final ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.UNDER_WIDGETS);
		setPriority(Overlay.PRIORITY_HIGH);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (config.obstaclesOverlay())
		{
			renderObstacles(graphics2D);
		}

		if (config.currentOverlay())
		{
			renderCurrents(graphics2D);
		}

		if (config.bubbleOverlay())
		{
			renderBubbles(graphics2D);
		}

		if (config.holeOverlay())
		{
			renderHoles(graphics2D);
		}

		if (config.pufferFishOverlay())
		{
			renderPufferFish(graphics2D);
		}

		if (config.chestClamOverlay())
		{
			renderChestsClams(graphics2D);
		}

		if (config.oxygenBar() != UWAConfig.OxygenBar.OFF)
		{
			renderOxygenBar(graphics2D);
		}

		return null;
	}

	private void renderHoles(final Graphics2D graphics2D)
	{
		final var gameObjects = plugin.getHoles();

		if (gameObjects.isEmpty())
		{
			return;
		}

		for (final var gameObject : gameObjects)
		{
			final Shape shape = gameObject.getClickbox();

			if (shape == null)
			{
				continue;
			}

			graphics2D.setStroke(STROKE);
			graphics2D.setColor(config.holeOutlineColor());
			graphics2D.draw(shape);
			graphics2D.setColor(config.holeFillColor());
			graphics2D.fill(shape);
		}
	}

	private void renderObstacles(final Graphics2D graphics2D)
	{
		final var gameObjects = plugin.getObstacles();

		if (gameObjects.isEmpty())
		{
			return;
		}

		for (final var gameObject : gameObjects)
		{
			final Shape shape = gameObject.getClickbox();

			if (shape == null)
			{
				continue;
			}

			graphics2D.setStroke(STROKE);
			graphics2D.setColor(config.obstaclesOutlineColor());
			graphics2D.draw(shape);

			graphics2D.setColor(config.obstaclesFillColor());
			graphics2D.fill(shape);
		}
	}

	private void renderCurrents(final Graphics2D graphics2D)
	{
		final var gameObjects = plugin.getCurrents();

		if (gameObjects.isEmpty())
		{
			return;
		}

		for (final var gameObject : gameObjects)
		{
			final Polygon polygon = gameObject.getCanvasTilePoly();

			if (polygon == null)
			{
				continue;
			}

			graphics2D.setStroke(STROKE);
			graphics2D.setColor(config.currentOutlineColor());
			graphics2D.draw(polygon);

			graphics2D.setColor(config.currentFillColor());
			graphics2D.fill(polygon);
		}
	}

	private void renderBubbles(final Graphics2D graphics2D)
	{
		final var gameObjects = plugin.getBubbles();

		if (gameObjects.isEmpty())
		{
			return;
		}

		for (final var gameObject : gameObjects)
		{
			final Polygon polygon = gameObject.getCanvasTilePoly();

			if (polygon == null)
			{
				continue;
			}

			graphics2D.setStroke(STROKE);
			graphics2D.setColor(config.bubbleOutlineColor());
			graphics2D.draw(polygon);

			graphics2D.setColor(config.bubbleFillColor());
			graphics2D.fill(polygon);
		}
	}

	private void renderPufferFish(final Graphics2D graphics2D)
	{
		final var npcs = plugin.getPufferFish();

		if (npcs.isEmpty())
		{
			return;
		}

		for (final NPC npc : npcs)
		{
			modelOutlineRenderer.drawOutline(npc, 2, config.pufferFishOutlineColor(), 4);

			final Shape shape = npc.getConvexHull();

			if (shape == null)
			{
				continue;
			}

			graphics2D.setColor(config.pufferFishFillColor());
			graphics2D.fill(shape);
		}
	}

	private void renderChestsClams(final Graphics2D graphics2D)
	{
		final var gameObjects = plugin.getOpenChestClams();

		if (gameObjects.isEmpty())
		{
			return;
		}

		for (final var gameObject : gameObjects)
		{
			final Shape shape = gameObject.getClickbox();

			if (shape == null)
			{
				continue;
			}

			modelOutlineRenderer.drawOutline(gameObject, 3, config.chestClamOutlineColor(), 4);
			graphics2D.setColor(config.chestClamFillColor());
			graphics2D.fill(shape);
		}
	}

	private void renderOxygenBar(final Graphics2D graphics2D)
	{
		int oxygen = plugin.getOxygen();

		if (oxygen <= 0)
		{
			return;
		}

		final String text;

		switch (config.oxygenBar())
		{
			case PERCENT:
				oxygen = oxygen / 10;
				text = String.format("%d%%", oxygen);
				break;
			case TICKS:
				oxygen = oxygen < 1000 ? plugin.getOxygenTicks() : (oxygen * 3) / 10;
				text = Integer.toString(oxygen);
				break;
			default:
				return;
		}

		final Point point = client.getLocalPlayer().getCanvasTextLocation(graphics2D, text, 0);

		if (point == null)
		{
			return;
		}

		final Color color = ColorUtil.colorLerp(Color.RED, Color.GREEN, Math.min(1, (double) plugin.getOxygen() / 1000));
		renderTextLocation(graphics2D, point, text, color);
	}

	private static void renderTextLocation(
		final Graphics2D graphics2D,
		final Point point,
		final String text,
		final Color color)
	{
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics2D.setFont(FontManager.getRunescapeBoldFont());
		graphics2D.setColor(Color.BLACK);

		final int x = point.getX();
		final int y = point.getY();

		renderTextBackground(graphics2D, x, y + 1, text);
		renderTextBackground(graphics2D, x, y - 1, text);
		renderTextBackground(graphics2D, x + 1, y, text);
		renderTextBackground(graphics2D, x - 1, y, text);

		graphics2D.setColor(color);
		graphics2D.drawString(text, x, y);
	}

	private static void renderTextBackground(
		final Graphics2D graphics2D,
		final int x, final int y,
		final String text)
	{
		graphics2D.drawString(text, x + 1, y + 1);
		graphics2D.drawString(text, x, y);
	}
}
