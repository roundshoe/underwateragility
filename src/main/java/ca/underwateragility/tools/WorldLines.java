/*
 * Copyright (c) 2021, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package ca.underwateragility.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.List;
import javax.annotation.Nonnull;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.OverlayUtil;

public class WorldLines
{
	private static final Stroke STROKE = new BasicStroke(1);

	public static void drawLinesOnWorld(
		final Graphics2D graphics,
		final Client client,
		final List<WorldPoint> linePoints,
		final Color color)
	{
		for (int i = 0; i < linePoints.size() - 1; i++)
		{
			final WorldPoint startWp = linePoints.get(i);
			final WorldPoint endWp = linePoints.get(i + 1);

			if (startWp == null || endWp == null)
			{
				continue;
			}
			if (startWp.equals(new WorldPoint(0, 0, 0)))
			{
				continue;
			}
			if (endWp.equals(new WorldPoint(0, 0, 0)))
			{
				continue;
			}
			if (startWp.getPlane() != endWp.getPlane())
			{
				continue;
			}
			LocalPoint startLp = LocalPoint.fromWorld(client.getTopLevelWorldView(), startWp);
			LocalPoint endLp = LocalPoint.fromWorld(client.getTopLevelWorldView(), endWp);
			if (startLp == null && endLp == null)
			{
				continue;
			}

			final int MAX_LP = 13056;

			if (endLp == null)
			{
				// Work out point of intersection of loaded area
				final int xDiff = endWp.getX() - startWp.getX();
				final int yDiff = endWp.getY() - startWp.getY();

				final int changeToGetXToBorder;
				if (xDiff != 0)
				{
					int goalLine = 0;
					if (xDiff > 0)
					{
						goalLine = MAX_LP;
					}
					changeToGetXToBorder = (goalLine - startLp.getX()) / xDiff;
				}
				else
				{
					changeToGetXToBorder = Integer.MAX_VALUE;
				}
				final int changeToGetYToBorder;
				if (yDiff != 0)
				{
					int goalLine = 0;
					if (yDiff > 0)
					{
						goalLine = MAX_LP;
					}
					changeToGetYToBorder = (goalLine - startLp.getY()) / yDiff;
				}
				else
				{
					changeToGetYToBorder = Integer.MAX_VALUE;
				}
				if (Math.abs(changeToGetXToBorder) < Math.abs(changeToGetYToBorder))
				{
					endLp = new LocalPoint(startLp.getX() + (xDiff * changeToGetXToBorder), startLp.getY() + (yDiff * changeToGetXToBorder), client.getTopLevelWorldView());
				}
				else
				{
					endLp = new LocalPoint(startLp.getX() + (xDiff * changeToGetYToBorder), startLp.getY() + (yDiff * changeToGetYToBorder), client.getTopLevelWorldView());
				}
			}

			if (startLp == null)
			{
				// Work out point of intersection of loaded area
				final int xDiff = startWp.getX() - endWp.getX();
				final int yDiff = startWp.getY() - endWp.getY();

				// if diff negative, go to 0?
				final int changeToGetXToBorder;
				if (xDiff != 0)
				{
					int goalLine = 0;
					if (xDiff > 0)
					{
						goalLine = MAX_LP;
					}
					changeToGetXToBorder = (goalLine - endLp.getX()) / xDiff;
				}
				else
				{
					changeToGetXToBorder = 1000000000;
				}
				final int changeToGetYToBorder;
				if (yDiff != 0)
				{
					int goalLine = 0;
					if (yDiff > 0)
					{
						goalLine = MAX_LP;
					}
					changeToGetYToBorder = (goalLine - endLp.getY()) / yDiff;
				}
				else
				{
					changeToGetYToBorder = 1000000000;
				}

				if (Math.abs(changeToGetXToBorder) < Math.abs(changeToGetYToBorder))
				{
					startLp = new LocalPoint(endLp.getX() + (xDiff * changeToGetXToBorder), endLp.getY() + (yDiff * changeToGetXToBorder), client.getTopLevelWorldView());
				}
				else
				{
					startLp = new LocalPoint(endLp.getX() + (xDiff * changeToGetYToBorder), endLp.getY() + (yDiff * changeToGetYToBorder), client.getTopLevelWorldView());
				}
			}

			// If one is in scene, find local point we intersect with

			final Line2D.Double newLine = getWorldLines(client, startLp, endLp);
			if (newLine != null)
			{
				OverlayUtil.renderPolygon(graphics, newLine, color, STROKE);
			}
		}
	}

	private static Line2D.Double getWorldLines(
		@Nonnull final Client client,
		@Nonnull final LocalPoint startLocation,
		final LocalPoint endLocation)
	{
		final int plane = client.getTopLevelWorldView().getPlane();

		final int startX = startLocation.getX();
		final int startY = startLocation.getY();
		final int endX = endLocation.getX();
		final int endY = endLocation.getY();

		final int sceneX = startLocation.getSceneX();
		final int sceneY = startLocation.getSceneY();

		if (sceneX < 0 || sceneY < 0 || sceneX >= Constants.SCENE_SIZE || sceneY >= Constants.SCENE_SIZE)
		{
			return null;
		}

		final int startHeight = Perspective.getTileHeight(client, startLocation, plane);
		final int endHeight = Perspective.getTileHeight(client, endLocation, plane);

		final Point p1 = Perspective.localToCanvas(client, startX, startY, startHeight);
		final Point p2 = Perspective.localToCanvas(client, endX, endY, endHeight);

		if (p1 == null || p2 == null)
		{
			return null;
		}

		return new Line2D.Double(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}
