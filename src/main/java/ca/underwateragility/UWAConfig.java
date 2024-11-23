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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.util.ColorUtil;

@ConfigGroup(UWAConfig.CONFIG_GROUP)
public interface UWAConfig extends Config
{
	int DEFAULT_ALPHA = 40;

	String CONFIG_GROUP = "underwateragility";

	@ConfigItem(
		name = "Oxygen Overlay",
		description = "Overlay oxygen on the player.",
		position = 0,
		keyName = "oxygenBar"
	)
	default OxygenBar oxygenBar()
	{
		return OxygenBar.OFF;
	}

	@ConfigItem(
		name = "Obstacle Overlay",
		description = "Overlay obstacle clickboxes.",
		position = 1,
		keyName = "obstaclesOverlay"
	)
	default boolean obstaclesOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Obstacle Outline",
		description = "Obstacle outline color.",
		position = 2,
		keyName = "obstaclesOutlineColor"
	)
	default Color obstaclesOutlineColor()
	{
		return Color.GREEN;
	}

	@Alpha
	@ConfigItem(
		name = "Obstacle Fill",
		description = "Obstacle fill color.",
		position = 3,
		keyName = "obstaclesFillColor"
	)
	default Color obstaclesFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.GREEN, DEFAULT_ALPHA);
	}

	@ConfigItem(
		name = "Bubble Overlay",
		description = "Overlay bubbles.",
		position = 4,
		keyName = "bubbleOverlay"
	)
	default boolean bubbleOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Bubble Outline",
		description = "Bubbles outline color.",
		position = 5,
		keyName = "bubbleOutlineColor"
	)
	default Color bubbleOutlineColor()
	{
		return ColorUtil.colorWithAlpha(Color.CYAN, 0);
	}

	@Alpha
	@ConfigItem(
		name = "Bubble Fill",
		description = "Bubbles fill color.",
		position = 6,
		keyName = "bubbleFillColor"
	)
	default Color bubbleFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.CYAN, DEFAULT_ALPHA);
	}

	@ConfigItem(
		name = "Chest/Clam Overlay",
		description = "Outline active chest/clam.",
		position = 7,
		keyName = "chestClamOverlay"
	)
	default boolean chestClamOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Chest/Clam Outline",
		description = "Chest/clam outline color.",
		position = 8,
		keyName = "chestClamOutlineColor"
	)
	default Color chestClamOutlineColor()
	{
		return Color.MAGENTA;
	}

	@Alpha
	@ConfigItem(
		name = "Chest/Clam Fill",
		description = "Chest/clam fill color.",
		position = 9,
		keyName = "chestClamFillColor"
	)
	default Color chestClamFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.MAGENTA, DEFAULT_ALPHA);
	}

	@ConfigItem(
		name = "Current Overlay",
		description = "Outline currents.",
		position = 10,
		keyName = "currentOverlay"
	)
	default boolean currentOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Current Outline",
		description = "Current outline color.",
		position = 11,
		keyName = "currentOutlineColor"
	)
	default Color currentOutlineColor()
	{
		return ColorUtil.colorWithAlpha(Color.RED, 0);
	}

	@Alpha
	@ConfigItem(
		name = "Current Fill",
		description = "Current fill color.",
		position = 12,
		keyName = "currentFillColor"
	)
	default Color currentFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.RED, DEFAULT_ALPHA);
	}

	@ConfigItem(
		name = "Hole Overlay",
		description = "Outline holes.",
		position = 13,
		keyName = "holeOverlay"
	)
	default boolean holeOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Hole Outline",
		description = "Hole outline color.",
		position = 14,
		keyName = "holeOutlineColor"
	)
	default Color holeOutlineColor()
	{
		return Color.YELLOW;
	}

	@Alpha
	@ConfigItem(
		name = "Hole Fill",
		description = "Hole fill color.",
		position = 15,
		keyName = "holeFillColor"
	)
	default Color holeFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.YELLOW, DEFAULT_ALPHA);
	}

	@ConfigItem(
		name = "Puffer Fish Overlay",
		description = "Overlay puffer fish.",
		position = 16,
		keyName = "pufferFishOverlay"
	)
	default boolean pufferFishOverlay()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		name = "Puffer Fish Outline",
		description = "Puffer fish outline color.",
		position = 17,
		keyName = "pufferFishOutlineColor"
	)
	default Color pufferFishOutlineColor()
	{
		return Color.BLUE;
	}

	@Alpha
	@ConfigItem(
		name = "Puffer Fish Fill",
		description = "Puffer fish fill color.",
		position = 18,
		keyName = "pufferFishFillColor"
	)
	default Color pufferFishFillColor()
	{
		return ColorUtil.colorWithAlpha(Color.BLUE, DEFAULT_ALPHA);
	}

	String CONFIG_KEY_CHEST_CLAM_TIMER = "chestClamTimer";

	@ConfigItem(
		name = "Chest/Clam Timer",
		description = "Display an infobox timer when the chest/clam moves.",
		position = 19,
		keyName = CONFIG_KEY_CHEST_CLAM_TIMER
	)
	default boolean chestClamTimer()
	{
		return false;
	}

	@ConfigItem(
		name = "Hole Lines",
		description = "Draw lines between connecting holes.",
		position = 20,
		keyName = "holeLines"
	)
	default boolean holeLines()
	{
		return false;
	}

	@ConfigItem(
		name = "Hole Lines Press Key",
		description = "Draw lines between connecting holes only while holding down configured key.",
		position = 21,
		keyName = "holeLinesPressKey"
	)
	default boolean holeLinesPressKey()
	{
		return false;
	}

	@ConfigItem(
		name = "Hole Lines Key",
		description = "The key to press to display lines between connecting holes.",
		position = 22,
		keyName = "holeLinesKey"
	)
	default ModifierlessKeybind holeLinesKey()
	{
		return new ModifierlessKeybind(KeyEvent.VK_SHIFT, InputEvent.SHIFT_DOWN_MASK);
	}

	String CONFIG_KEY_HIDE_OXYGEN_WIDGET = "hideOxygenWidget";

	@ConfigItem(
		name = "Hide Oxygen Overlay",
		description = "Hide the oxygen overlay.",
		position = 23,
		keyName = CONFIG_KEY_HIDE_OXYGEN_WIDGET
	)
	default boolean hideOxygenWidget()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_WATER_WIDGET = "hideWaterWidget";

	@ConfigItem(
		name = "Hide Water Overlay",
		description = "Hide the water overlay.",
		position = 24,
		keyName = CONFIG_KEY_HIDE_WATER_WIDGET
	)
	default boolean hideWaterWidget()
	{
		return false;
	}

	String CONFIG_KEY_HIDE_SCENERY = "hideScenery";

	@ConfigItem(
		name = "Hide Scenery",
		description = "Hide scenery.",
		position = 25,
		keyName = CONFIG_KEY_HIDE_SCENERY
	)
	default boolean hideScenery()
	{
		return false;
	}

	// enum

	enum OxygenBar
	{
		OFF,
		PERCENT,
		TICKS
	}
}
