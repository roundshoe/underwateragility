/*
 * Copyright (c) 2020, R438
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
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Singleton
class UWAInformationOverlayPanel extends OverlayPanel
{
	private final UWAPlugin plugin;
	private final UWAConfig config;

	@Inject
	UWAInformationOverlayPanel(final UWAPlugin plugin, final UWAConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.BOTTOM_RIGHT);
		setPriority(Overlay.PRIORITY_HIGH);
	}

	@Override
	public Dimension render(final Graphics2D graphics2D)
	{
		if (!config.informationPanelOverlay())
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Underwater Agility")
			.color(Color.CYAN)
			.build());

		final int ticksRemaining = plugin.getTicksRemaining();

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Ticks:")
			.right(ticksRemaining >= 0 ? Integer.toString(ticksRemaining) : "?")
			.build());

		final int distance = plugin.getDistanceToTarget();

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Distance:")
			.right(distance >= 0 ? Integer.toString(distance) : "?")
			.build());

		final boolean adjacent = plugin.isBubbleAdjacent();

		final int oxygenTicks = plugin.getOxygenTicks();

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Oxygen:")
			.right(Integer.toString(oxygenTicks))
			.build());

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Bubble:")
			.right(adjacent ? "Yes" : "No")
			.rightColor(adjacent ? Color.GREEN : Color.RED)
			.build());

//		final var wp = plugin.getTargetWorldPoint();
//
//		if (wp != null)
//		{
//			panelComponent.getChildren().add(LineComponent.builder()
//				.left("Debug:")
//				.right(String.format("%d, %d", wp.getX(), wp.getY()))
//				.build());
//		}

		return super.render(graphics2D);
	}
}
