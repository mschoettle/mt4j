/***********************************************************************
 * mt4j Copyright (c) 2008 - 2010 C.Ruff, Fraunhofer-Gesellschaft All rights reserved.
 *  
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ***********************************************************************/
package org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor;

import java.util.Arrays;
import java.util.List;


import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.Direction;
import org.mt4j.input.inputProcessors.componentProcessors.unistrokeProcessor.UnistrokeUtils.UnistrokeGesture;
import org.mt4j.util.math.Vector3D;



/**
 * The Class MTDollarTemplates.
 */
public class UnistrokeTemplates {
	
	/** The List of registered templates. */
	List <Template> templates;

	/** The Utilities. */
	UnistrokeUtils du;
	
	/**
	 * Instantiates new MTDollarTemplates.
	 * 
	 * @param templates the List of registered templates
	 * @param du the Utils
	 */
	public UnistrokeTemplates (List<Template> templates, UnistrokeUtils du) {
		this.templates = templates;
		this.du = du;
	}
	
	
	
	
	/**
	 * The Class Template.
	 */
	class Template{
	  
  	/** The gesture. */
  	UnistrokeGesture gesture;
	  
  	/** The direction. */
  	Direction direction;
	  
  	/** The Points. */
  	List<Vector3D> Points;
	  
  	/**
  	 * Instantiates a new template.
  	 * 
  	 * @param gesture the gesture
  	 * @param points the points
  	 * @param direction the direction
  	 */
  	Template(UnistrokeGesture gesture, List<Vector3D> points, Direction direction){
	   this.gesture = gesture;
	   this.direction = direction;
	    Points = du.Resample( points, du.getNumPoints(), direction);
	    Points = du.RotateToZero( Points );
	    Points = du.ScaleToSquare( Points, du.getSquareSize());
	    Points = du.TranslateToOrigin( Points );

	  }
	}

	/**
	 * Adds the template by gesture/direction.
	 *
	 * @param gesture the gesture
	 * @param direction the direction
	 */
	public void addTemplate(UnistrokeGesture gesture, Direction direction) {
		List<Vector3D> points = null;
		switch (gesture) {
		case TRIANGLE:
			Vector3D[] point0_array = {
			 new Vector3D(137,139),new Vector3D(135,141),new Vector3D(133,144),new Vector3D(132,146),
             new Vector3D(130,149),new Vector3D(128,151),new Vector3D(126,155),new Vector3D(123,160),
             new Vector3D(120,166),new Vector3D(116,171),new Vector3D(112,177),new Vector3D(107,183),
             new Vector3D(102,188),new Vector3D(100,191),new Vector3D(95,195),new Vector3D(90,199),
             new Vector3D(86,203),new Vector3D(82,206),new Vector3D(80,209),new Vector3D(75,213),
             new Vector3D(73,213),new Vector3D(70,216),new Vector3D(67,219),new Vector3D(64,221),
             new Vector3D(61,223),new Vector3D(60,225),new Vector3D(62,226),new Vector3D(65,225),
             new Vector3D(67,226),new Vector3D(74,226),new Vector3D(77,227),new Vector3D(85,229),
             new Vector3D(91,230),new Vector3D(99,231),new Vector3D(108,232),new Vector3D(116,233),
             new Vector3D(125,233),new Vector3D(134,234),new Vector3D(145,233),new Vector3D(153,232),
             new Vector3D(160,233),new Vector3D(170,234),new Vector3D(177,235),new Vector3D(179,236),
             new Vector3D(186,237),new Vector3D(193,238),new Vector3D(198,239),new Vector3D(200,237),
             new Vector3D(202,239),new Vector3D(204,238),new Vector3D(206,234),new Vector3D(205,230),
             new Vector3D(202,222),new Vector3D(197,216),new Vector3D(192,207),new Vector3D(186,198),
             new Vector3D(179,189),new Vector3D(174,183),new Vector3D(170,178),new Vector3D(164,171),
             new Vector3D(161,168),new Vector3D(154,160),new Vector3D(148,155),new Vector3D(143,150),
             new Vector3D(138,148),new Vector3D(136,148) };
		points = Arrays.asList(point0_array);



		break;


		case X:
			Vector3D [] point1_array = {
				new Vector3D(87,142),new Vector3D(89,145),new Vector3D(91,148),new Vector3D(93,151),
				new Vector3D(96,155),new Vector3D(98,157),new Vector3D(100,160),new Vector3D(102,162),
				new Vector3D(106,167),new Vector3D(108,169),new Vector3D(110,171),new Vector3D(115,177),
				new Vector3D(119,183),new Vector3D(123,189),new Vector3D(127,193),new Vector3D(129,196),
				new Vector3D(133,200),new Vector3D(137,206),new Vector3D(140,209),new Vector3D(143,212),
				new Vector3D(146,215),new Vector3D(151,220),new Vector3D(153,222),new Vector3D(155,223),
             	new Vector3D(157,225),new Vector3D(158,223),new Vector3D(157,218),new Vector3D(155,211),
             	new Vector3D(154,208),new Vector3D(152,200),new Vector3D(150,189),new Vector3D(148,179),
             	new Vector3D(147,170),new Vector3D(147,158),new Vector3D(147,148),new Vector3D(147,141),
             	new Vector3D(147,136),new Vector3D(144,135),new Vector3D(142,137),new Vector3D(140,139),
             	new Vector3D(135,145),new Vector3D(131,152),new Vector3D(124,163),new Vector3D(116,177),
             	new Vector3D(108,191),new Vector3D(100,206),new Vector3D(94,217),new Vector3D(91,222),
             	new Vector3D(89,225),new Vector3D(87,226),new Vector3D(87,224) } ;
			points = Arrays.asList(point1_array);

			break;

		case RECTANGLE:
			Vector3D[] point2_array = {
				 new Vector3D(78,149),new Vector3D(78,153),new Vector3D(78,157),new Vector3D(78,160),
	             new Vector3D(79,162),new Vector3D(79,164),new Vector3D(79,167),new Vector3D(79,169),
	             new Vector3D(79,173),new Vector3D(79,178),new Vector3D(79,183),new Vector3D(80,189),
	             new Vector3D(80,193),new Vector3D(80,198),new Vector3D(80,202),new Vector3D(81,208),
	             new Vector3D(81,210),new Vector3D(81,216),new Vector3D(82,222),new Vector3D(82,224),
	             new Vector3D(82,227),new Vector3D(83,229),new Vector3D(83,231),new Vector3D(85,230),
	             new Vector3D(88,232),new Vector3D(90,233),new Vector3D(92,232),new Vector3D(94,233),
	             new Vector3D(99,232),new Vector3D(102,233),new Vector3D(106,233),new Vector3D(109,234),
	             new Vector3D(117,235),new Vector3D(123,236),new Vector3D(126,236),new Vector3D(135,237),
	             new Vector3D(142,238),new Vector3D(145,238),new Vector3D(152,238),new Vector3D(154,239),
	             new Vector3D(165,238),new Vector3D(174,237),new Vector3D(179,236),new Vector3D(186,235),
	             new Vector3D(191,235),new Vector3D(195,233),new Vector3D(197,233),new Vector3D(200,233),
	             new Vector3D(201,235),new Vector3D(201,233),new Vector3D(199,231),new Vector3D(198,226),
	             new Vector3D(198,220),new Vector3D(196,207),new Vector3D(195,195),new Vector3D(195,181),
	             new Vector3D(195,173),new Vector3D(195,163),new Vector3D(194,155),new Vector3D(192,145),
	             new Vector3D(192,143),new Vector3D(192,138),new Vector3D(191,135),new Vector3D(191,133),
	             new Vector3D(191,130),new Vector3D(190,128),new Vector3D(188,129),new Vector3D(186,129),
	             new Vector3D(181,132),new Vector3D(173,131),new Vector3D(162,131),new Vector3D(151,132),
	             new Vector3D(149,132),new Vector3D(138,132),new Vector3D(136,132),new Vector3D(122,131),
	             new Vector3D(120,131),new Vector3D(109,130),new Vector3D(107,130),new Vector3D(90,132),
	             new Vector3D(81,133),new Vector3D(76,133)};
			points = Arrays.asList(point2_array);

			break;

		case CIRCLE:
			Vector3D[] point3_array = {
				  new Vector3D(127,141),new Vector3D(124,140),new Vector3D(120,139),new Vector3D(118,139),
	              new Vector3D(116,139),new Vector3D(111,140),new Vector3D(109,141),new Vector3D(104,144),
	              new Vector3D(100,147),new Vector3D(96,152),new Vector3D(93,157),new Vector3D(90,163),
	              new Vector3D(87,169),new Vector3D(85,175),new Vector3D(83,181),new Vector3D(82,190),
	              new Vector3D(82,195),new Vector3D(83,200),new Vector3D(84,205),new Vector3D(88,213),
	              new Vector3D(91,216),new Vector3D(96,219),new Vector3D(103,222),new Vector3D(108,224),
	              new Vector3D(111,224),new Vector3D(120,224),new Vector3D(133,223),new Vector3D(142,222),
	              new Vector3D(152,218),new Vector3D(160,214),new Vector3D(167,210),new Vector3D(173,204),
	              new Vector3D(178,198),new Vector3D(179,196),new Vector3D(182,188),new Vector3D(182,177),
	              new Vector3D(178,167),new Vector3D(170,150),new Vector3D(163,138),new Vector3D(152,130),
	              new Vector3D(143,129),new Vector3D(140,131),new Vector3D(129,136),new Vector3D(126,139)};
			points = Arrays.asList(point3_array);

			break;

		case CHECK:
			Vector3D[] point4_array = {
				 new Vector3D(91,185),new Vector3D(93,185),new Vector3D(95,185),new Vector3D(97,185),new Vector3D(100,188),
	             new Vector3D(102,189),new Vector3D(104,190),new Vector3D(106,193),new Vector3D(108,195),new Vector3D(110,198),
	             new Vector3D(112,201),new Vector3D(114,204),new Vector3D(115,207),new Vector3D(117,210),new Vector3D(118,212),
	             new Vector3D(120,214),new Vector3D(121,217),new Vector3D(122,219),new Vector3D(123,222),new Vector3D(124,224),
	             new Vector3D(126,226),new Vector3D(127,229),new Vector3D(129,231),new Vector3D(130,233),new Vector3D(129,231),
	             new Vector3D(129,228),new Vector3D(129,226),new Vector3D(129,224),new Vector3D(129,221),new Vector3D(129,218),
	             new Vector3D(129,212),new Vector3D(129,208),new Vector3D(130,198),new Vector3D(132,189),new Vector3D(134,182),
	             new Vector3D(137,173),new Vector3D(143,164),new Vector3D(147,157),new Vector3D(151,151),new Vector3D(155,144),
	             new Vector3D(161,137),new Vector3D(165,131),new Vector3D(171,122),new Vector3D(174,118),new Vector3D(176,114),
	             new Vector3D(177,112),new Vector3D(177,114),new Vector3D(175,116),new Vector3D(173,118) };
			points = Arrays.asList(point4_array);
;
			break;

		case CARET:
			Vector3D[] point5_array = {
				  new Vector3D(79,245),new Vector3D(79,242),new Vector3D(79,239),new Vector3D(80,237),new Vector3D(80,234),
	              new Vector3D(81,232),new Vector3D(82,230),new Vector3D(84,224),new Vector3D(86,220),new Vector3D(86,218),
	              new Vector3D(87,216),new Vector3D(88,213),new Vector3D(90,207),new Vector3D(91,202),new Vector3D(92,200),
	              new Vector3D(93,194),new Vector3D(94,192),new Vector3D(96,189),new Vector3D(97,186),new Vector3D(100,179),
	              new Vector3D(102,173),new Vector3D(105,165),new Vector3D(107,160),new Vector3D(109,158),new Vector3D(112,151),
	              new Vector3D(115,144),new Vector3D(117,139),new Vector3D(119,136),new Vector3D(119,134),new Vector3D(120,132),
	              new Vector3D(121,129),new Vector3D(122,127),new Vector3D(124,125),new Vector3D(126,124),new Vector3D(129,125),
	              new Vector3D(131,127),new Vector3D(132,130),new Vector3D(136,139),new Vector3D(141,154),new Vector3D(145,166),
	              new Vector3D(151,182),new Vector3D(156,193),new Vector3D(157,196),new Vector3D(161,209),new Vector3D(162,211),
	              new Vector3D(167,223),new Vector3D(169,229),new Vector3D(170,231),new Vector3D(173,237),new Vector3D(176,242),
	              new Vector3D(177,244),new Vector3D(179,250),new Vector3D(181,255),new Vector3D(182,257) };
			points = Arrays.asList(point5_array);

			break;

		case QUESTION:
			Vector3D[] point6_array = {
				  new Vector3D(104,145),new Vector3D(103,142),new Vector3D(103,140),new Vector3D(103,138),new Vector3D(103,135),
	              new Vector3D(104,133),new Vector3D(105,131),new Vector3D(106,128),new Vector3D(107,125),new Vector3D(108,123),
	              new Vector3D(111,121),new Vector3D(113,118),new Vector3D(115,116),new Vector3D(117,116),new Vector3D(119,116),
	              new Vector3D(121,115),new Vector3D(124,116),new Vector3D(126,115),new Vector3D(128,114),new Vector3D(130,115),
	              new Vector3D(133,116),new Vector3D(135,117),new Vector3D(140,120),new Vector3D(142,121),new Vector3D(144,123),
	              new Vector3D(146,125),new Vector3D(149,127),new Vector3D(150,129),new Vector3D(152,130),new Vector3D(154,132),
	              new Vector3D(156,134),new Vector3D(158,137),new Vector3D(159,139),new Vector3D(160,141),new Vector3D(160,143),
	              new Vector3D(160,146),new Vector3D(160,149),new Vector3D(159,153),new Vector3D(158,155),new Vector3D(157,157),
	              new Vector3D(155,159),new Vector3D(153,161),new Vector3D(151,163),new Vector3D(146,167),new Vector3D(142,170),
	              new Vector3D(138,172),new Vector3D(134,173),new Vector3D(132,175),new Vector3D(127,175),new Vector3D(124,175),
	              new Vector3D(122,176),new Vector3D(120,178),new Vector3D(119,180),new Vector3D(119,183),new Vector3D(119,185),
	              new Vector3D(120,190),new Vector3D(121,194),new Vector3D(122,200),new Vector3D(123,205),new Vector3D(123,211),
	              new Vector3D(124,215),new Vector3D(124,223),new Vector3D(124,225)};
			points = Arrays.asList(point6_array);
			break;


		case ARROW:
			Vector3D[] point7_array = {
					new Vector3D(68,222),new Vector3D(70,220),new Vector3D(73,218),new Vector3D(75,217),
					new Vector3D(77,215),new Vector3D(80,213),new Vector3D(82,212),new Vector3D(84,210),
					new Vector3D(87,209),new Vector3D(89,208),new Vector3D(92,206),new Vector3D(95,204),
					new Vector3D(101,201),new Vector3D(106,198),new Vector3D(112,194),new Vector3D(118,191),
					new Vector3D(124,187),new Vector3D(127,186),new Vector3D(132,183),new Vector3D(138,181),
					new Vector3D(141,180),new Vector3D(146,178),new Vector3D(154,173),new Vector3D(159,171),
					new Vector3D(161,170),new Vector3D(166,167),new Vector3D(168,167),new Vector3D(171,166),
					new Vector3D(174,164),new Vector3D(177,162),new Vector3D(180,160),new Vector3D(182,158),
					new Vector3D(183,156),new Vector3D(181,154),new Vector3D(178,153),new Vector3D(171,153),
					new Vector3D(164,153),new Vector3D(160,153),new Vector3D(150,154),new Vector3D(147,155),
					new Vector3D(141,157),new Vector3D(137,158),new Vector3D(135,158),new Vector3D(137,158),
					new Vector3D(140,157),new Vector3D(143,156),new Vector3D(151,154),new Vector3D(160,152),
					new Vector3D(170,149),new Vector3D(179,147),new Vector3D(185,145),new Vector3D(192,144),
					new Vector3D(196,144),new Vector3D(198,144),new Vector3D(200,144),new Vector3D(201,147),
					new Vector3D(199,149),new Vector3D(194,157),new Vector3D(191,160),new Vector3D(186,167),
					new Vector3D(180,176),new Vector3D(177,179),new Vector3D(171,187),new Vector3D(169,189),
					new Vector3D(165,194),new Vector3D(164,196)};
			points = Arrays.asList(point7_array);

			break;

		case LEFTSQUAREBRACKET:
			Vector3D[] point8_array = {
					new Vector3D(140,124),new Vector3D(138,123),new Vector3D(135,122),new Vector3D(133,123),
					new Vector3D(130,123),new Vector3D(128,124),new Vector3D(125,125),new Vector3D(122,124),
					new Vector3D(120,124),new Vector3D(118,124),new Vector3D(116,125),new Vector3D(113,125),
					new Vector3D(111,125),new Vector3D(108,124),new Vector3D(106,125),new Vector3D(104,125),
					new Vector3D(102,124),new Vector3D(100,123),new Vector3D(98,123),new Vector3D(95,124),
					new Vector3D(93,123),new Vector3D(90,124),new Vector3D(88,124),new Vector3D(85,125),
					new Vector3D(83,126),new Vector3D(81,127),new Vector3D(81,129),new Vector3D(82,131),
					new Vector3D(82,134),new Vector3D(83,138),new Vector3D(84,141),new Vector3D(84,144),
					new Vector3D(85,148),new Vector3D(85,151),new Vector3D(86,156),new Vector3D(86,160),
					new Vector3D(86,164),new Vector3D(86,168),new Vector3D(87,171),new Vector3D(87,175),
					new Vector3D(87,179),new Vector3D(87,182),new Vector3D(87,186),new Vector3D(88,188),
					new Vector3D(88,195),new Vector3D(88,198),new Vector3D(88,201),new Vector3D(88,207),
					new Vector3D(89,211),new Vector3D(89,213),new Vector3D(89,217),new Vector3D(89,222),
					new Vector3D(88,225),new Vector3D(88,229),new Vector3D(88,231),new Vector3D(88,233),
					new Vector3D(88,235),new Vector3D(89,237),new Vector3D(89,240),new Vector3D(89,242),
					new Vector3D(91,241),new Vector3D(94,241),new Vector3D(96,240),new Vector3D(98,239),
					new Vector3D(105,240),new Vector3D(109,240),new Vector3D(113,239),new Vector3D(116,240),
					new Vector3D(121,239),new Vector3D(130,240),new Vector3D(136,237),new Vector3D(139,237),
					new Vector3D(144,238),new Vector3D(151,237),new Vector3D(157,236),new Vector3D(159,237)};
			points = Arrays.asList(point8_array);

			break;

		case RIGHTSQUAREBRACKET:
			Vector3D[] point9_array = {
					new Vector3D(112,138),new Vector3D(112,136),new Vector3D(115,136),new Vector3D(118,137),
					new Vector3D(120,136),new Vector3D(123,136),new Vector3D(125,136),new Vector3D(128,136),
					new Vector3D(131,136),new Vector3D(134,135),new Vector3D(137,135),new Vector3D(140,134),
					new Vector3D(143,133),new Vector3D(145,132),new Vector3D(147,132),new Vector3D(149,132),
					new Vector3D(152,132),new Vector3D(153,134),new Vector3D(154,137),new Vector3D(155,141),
					new Vector3D(156,144),new Vector3D(157,152),new Vector3D(158,161),new Vector3D(160,170),
					new Vector3D(162,182),new Vector3D(164,192),new Vector3D(166,200),new Vector3D(167,209),
					new Vector3D(168,214),new Vector3D(168,216),new Vector3D(169,221),new Vector3D(169,223),
					new Vector3D(169,228),new Vector3D(169,231),new Vector3D(166,233),new Vector3D(164,234),
					new Vector3D(161,235),new Vector3D(155,236),new Vector3D(147,235),new Vector3D(140,233),
					new Vector3D(131,233),new Vector3D(124,233),new Vector3D(117,235),new Vector3D(114,238),
					new Vector3D(112,238)};
			points = Arrays.asList(point9_array);

			break;


		case V:
			Vector3D[] point10_array = {
					new Vector3D(89,164),new Vector3D(90,162),new Vector3D(92,162),new Vector3D(94,164),
					new Vector3D(95,166),new Vector3D(96,169),new Vector3D(97,171),new Vector3D(99,175),
					new Vector3D(101,178),new Vector3D(103,182),new Vector3D(106,189),new Vector3D(108,194),
					new Vector3D(111,199),new Vector3D(114,204),new Vector3D(117,209),new Vector3D(119,214),
					new Vector3D(122,218),new Vector3D(124,222),new Vector3D(126,225),new Vector3D(128,228),
					new Vector3D(130,229),new Vector3D(133,233),new Vector3D(134,236),new Vector3D(136,239),
					new Vector3D(138,240),new Vector3D(139,242),new Vector3D(140,244),new Vector3D(142,242),
					new Vector3D(142,240),new Vector3D(142,237),new Vector3D(143,235),new Vector3D(143,233),
					new Vector3D(145,229),new Vector3D(146,226),new Vector3D(148,217),new Vector3D(149,208),
					new Vector3D(149,205),new Vector3D(151,196),new Vector3D(151,193),new Vector3D(153,182),
					new Vector3D(155,172),new Vector3D(157,165),new Vector3D(159,160),new Vector3D(162,155),
					new Vector3D(164,150),new Vector3D(165,148),new Vector3D(166,146)};
			points = Arrays.asList(point10_array);

			break;

		case DELETE:
			Vector3D[] point11_array = {
					new Vector3D(123,129),new Vector3D(123,131),new Vector3D(124,133),new Vector3D(125,136),
					new Vector3D(127,140),new Vector3D(129,142),new Vector3D(133,148),new Vector3D(137,154),
					new Vector3D(143,158),new Vector3D(145,161),new Vector3D(148,164),new Vector3D(153,170),
					new Vector3D(158,176),new Vector3D(160,178),new Vector3D(164,183),new Vector3D(168,188),
					new Vector3D(171,191),new Vector3D(175,196),new Vector3D(178,200),new Vector3D(180,202),
					new Vector3D(181,205),new Vector3D(184,208),new Vector3D(186,210),new Vector3D(187,213),
					new Vector3D(188,215),new Vector3D(186,212),new Vector3D(183,211),new Vector3D(177,208),
					new Vector3D(169,206),new Vector3D(162,205),new Vector3D(154,207),new Vector3D(145,209),
					new Vector3D(137,210),new Vector3D(129,214),new Vector3D(122,217),new Vector3D(118,218),
					new Vector3D(111,221),new Vector3D(109,222),new Vector3D(110,219),new Vector3D(112,217),
					new Vector3D(118,209),new Vector3D(120,207),new Vector3D(128,196),new Vector3D(135,187),
					new Vector3D(138,183),new Vector3D(148,167),new Vector3D(157,153),new Vector3D(163,145),
					new Vector3D(165,142),new Vector3D(172,133),new Vector3D(177,127),new Vector3D(179,127),
					new Vector3D(180,125)};
			points = Arrays.asList(point11_array);

			break;

		case LEFTCURLYBRACE:
			Vector3D[] point12_array = {
					new Vector3D(150,116),new Vector3D(147,117),new Vector3D(145,116),new Vector3D(142,116),
					new Vector3D(139,117),new Vector3D(136,117),new Vector3D(133,118),new Vector3D(129,121),
					new Vector3D(126,122),new Vector3D(123,123),new Vector3D(120,125),new Vector3D(118,127),
					new Vector3D(115,128),new Vector3D(113,129),new Vector3D(112,131),new Vector3D(113,134),
					new Vector3D(115,134),new Vector3D(117,135),new Vector3D(120,135),new Vector3D(123,137),
					new Vector3D(126,138),new Vector3D(129,140),new Vector3D(135,143),new Vector3D(137,144),
					new Vector3D(139,147),new Vector3D(141,149),new Vector3D(140,152),new Vector3D(139,155),
					new Vector3D(134,159),new Vector3D(131,161),new Vector3D(124,166),new Vector3D(121,166),
					new Vector3D(117,166),new Vector3D(114,167),new Vector3D(112,166),new Vector3D(114,164),
					new Vector3D(116,163),new Vector3D(118,163),new Vector3D(120,162),new Vector3D(122,163),
					new Vector3D(125,164),new Vector3D(127,165),new Vector3D(129,166),new Vector3D(130,168),
					new Vector3D(129,171),new Vector3D(127,175),new Vector3D(125,179),new Vector3D(123,184),
					new Vector3D(121,190),new Vector3D(120,194),new Vector3D(119,199),new Vector3D(120,202),
					new Vector3D(123,207),new Vector3D(127,211),new Vector3D(133,215),new Vector3D(142,219),
					new Vector3D(148,220),new Vector3D(151,221)};
			points = Arrays.asList(point12_array);

			break;

		case RIGHTCURLYBRACE:
			Vector3D[] point13_array = {
					new Vector3D(117,132),new Vector3D(115,132),new Vector3D(115,129),new Vector3D(117,129),
					new Vector3D(119,128),new Vector3D(122,127),new Vector3D(125,127),new Vector3D(127,127),
					new Vector3D(130,127),new Vector3D(133,129),new Vector3D(136,129),new Vector3D(138,130),
					new Vector3D(140,131),new Vector3D(143,134),new Vector3D(144,136),new Vector3D(145,139),
					new Vector3D(145,142),new Vector3D(145,145),new Vector3D(145,147),new Vector3D(145,149),
					new Vector3D(144,152),new Vector3D(142,157),new Vector3D(141,160),new Vector3D(139,163),
					new Vector3D(137,166),new Vector3D(135,167),new Vector3D(133,169),new Vector3D(131,172),
					new Vector3D(128,173),new Vector3D(126,176),new Vector3D(125,178),new Vector3D(125,180),
					new Vector3D(125,182),new Vector3D(126,184),new Vector3D(128,187),new Vector3D(130,187),
					new Vector3D(132,188),new Vector3D(135,189),new Vector3D(140,189),new Vector3D(145,189),
					new Vector3D(150,187),new Vector3D(155,186),new Vector3D(157,185),new Vector3D(159,184),
					new Vector3D(156,185),new Vector3D(154,185),new Vector3D(149,185),new Vector3D(145,187),
					new Vector3D(141,188),new Vector3D(136,191),new Vector3D(134,191),new Vector3D(131,192),
					new Vector3D(129,193),new Vector3D(129,195),new Vector3D(129,197),new Vector3D(131,200),
					new Vector3D(133,202),new Vector3D(136,206),new Vector3D(139,211),new Vector3D(142,215),
					new Vector3D(145,220),new Vector3D(147,225),new Vector3D(148,231),new Vector3D(147,239),
					new Vector3D(144,244),new Vector3D(139,248),new Vector3D(134,250),new Vector3D(126,253),
					new Vector3D(119,253),new Vector3D(115,253)};
			points = Arrays.asList(point13_array);

			break;

		case STAR:
			Vector3D[] point14_array = {
					new Vector3D(75,250),new Vector3D(75,247),new Vector3D(77,244),new Vector3D(78,242),
					new Vector3D(79,239),new Vector3D(80,237),new Vector3D(82,234),new Vector3D(82,232),
					new Vector3D(84,229),new Vector3D(85,225),new Vector3D(87,222),new Vector3D(88,219),
					new Vector3D(89,216),new Vector3D(91,212),new Vector3D(92,208),new Vector3D(94,204),
					new Vector3D(95,201),new Vector3D(96,196),new Vector3D(97,194),new Vector3D(98,191),
					new Vector3D(100,185),new Vector3D(102,178),new Vector3D(104,173),new Vector3D(104,171),
					new Vector3D(105,164),new Vector3D(106,158),new Vector3D(107,156),new Vector3D(107,152),
					new Vector3D(108,145),new Vector3D(109,141),new Vector3D(110,139),new Vector3D(112,133),
					new Vector3D(113,131),new Vector3D(116,127),new Vector3D(117,125),new Vector3D(119,122),
					new Vector3D(121,121),new Vector3D(123,120),new Vector3D(125,122),new Vector3D(125,125),
					new Vector3D(127,130),new Vector3D(128,133),new Vector3D(131,143),new Vector3D(136,153),
					new Vector3D(140,163),new Vector3D(144,172),new Vector3D(145,175),new Vector3D(151,189),
					new Vector3D(156,201),new Vector3D(161,213),new Vector3D(166,225),new Vector3D(169,233),
					new Vector3D(171,236),new Vector3D(174,243),new Vector3D(177,247),new Vector3D(178,249),
					new Vector3D(179,251),new Vector3D(180,253),new Vector3D(180,255),new Vector3D(179,257),
					new Vector3D(177,257),new Vector3D(174,255),new Vector3D(169,250),new Vector3D(164,247),
					new Vector3D(160,245),new Vector3D(149,238),new Vector3D(138,230),new Vector3D(127,221),
					new Vector3D(124,220),new Vector3D(112,212),new Vector3D(110,210),new Vector3D(96,201),
					new Vector3D(84,195),new Vector3D(74,190),new Vector3D(64,182),new Vector3D(55,175),
					new Vector3D(51,172),new Vector3D(49,170),new Vector3D(51,169),new Vector3D(56,169),
					new Vector3D(66,169),new Vector3D(78,168),new Vector3D(92,166),new Vector3D(107,164),
					new Vector3D(123,161),new Vector3D(140,162),new Vector3D(156,162),new Vector3D(171,160),
					new Vector3D(173,160),new Vector3D(186,160),new Vector3D(195,160),new Vector3D(198,161),
					new Vector3D(203,163),new Vector3D(208,163),new Vector3D(206,164),new Vector3D(200,167),
					new Vector3D(187,172),new Vector3D(174,179),new Vector3D(172,181),new Vector3D(153,192),
					new Vector3D(137,201),new Vector3D(123,211),new Vector3D(112,220),new Vector3D(99,229),
					new Vector3D(90,237),new Vector3D(80,244),new Vector3D(73,250),new Vector3D(69,254),
					new Vector3D(69,252)};
			points = Arrays.asList(point14_array);

			break;
		case PIGTAIL:
			Vector3D[] point15_array = {
					new Vector3D(81,219),new Vector3D(84,218),new Vector3D(86,220),new Vector3D(88,220),
					new Vector3D(90,220),new Vector3D(92,219),new Vector3D(95,220),new Vector3D(97,219),
					new Vector3D(99,220),new Vector3D(102,218),new Vector3D(105,217),new Vector3D(107,216),
					new Vector3D(110,216),new Vector3D(113,214),new Vector3D(116,212),new Vector3D(118,210),
					new Vector3D(121,208),new Vector3D(124,205),new Vector3D(126,202),new Vector3D(129,199),
					new Vector3D(132,196),new Vector3D(136,191),new Vector3D(139,187),new Vector3D(142,182),
					new Vector3D(144,179),new Vector3D(146,174),new Vector3D(148,170),new Vector3D(149,168),
					new Vector3D(151,162),new Vector3D(152,160),new Vector3D(152,157),new Vector3D(152,155),
					new Vector3D(152,151),new Vector3D(152,149),new Vector3D(152,146),new Vector3D(149,142),
					new Vector3D(148,139),new Vector3D(145,137),new Vector3D(141,135),new Vector3D(139,135),
					new Vector3D(134,136),new Vector3D(130,140),new Vector3D(128,142),new Vector3D(126,145),
					new Vector3D(122,150),new Vector3D(119,158),new Vector3D(117,163),new Vector3D(115,170),
					new Vector3D(114,175),new Vector3D(117,184),new Vector3D(120,190),new Vector3D(125,199),
					new Vector3D(129,203),new Vector3D(133,208),new Vector3D(138,213),new Vector3D(145,215),
					new Vector3D(155,218),new Vector3D(164,219),new Vector3D(166,219),new Vector3D(177,219),
					new Vector3D(182,218),new Vector3D(192,216),new Vector3D(196,213),new Vector3D(199,212),
					new Vector3D(201,211)};
			points = Arrays.asList(point15_array);
			break;

		case PACKAGE:
			Vector3D[] point20_array = {
					new Vector3D(332,174),new Vector3D(347,173),new Vector3D(363,171),new Vector3D(382,168),
					new Vector3D(390,166),new Vector3D(405,163),new Vector3D(419,162),new Vector3D(421,172),
					new Vector3D(422,186),new Vector3D(421,203),new Vector3D(419,213),new Vector3D(417,233),
					new Vector3D(416,244),new Vector3D(413,261),new Vector3D(411,275),new Vector3D(413,283),
					new Vector3D(427,284),new Vector3D(439,283),new Vector3D(447,284),new Vector3D(467,282),
					new Vector3D(477,280),new Vector3D(493,279),new Vector3D(508,277),new Vector3D(520,277),
					new Vector3D(525,284),new Vector3D(525,295),new Vector3D(522,315),new Vector3D(519,328),
					new Vector3D(517,340),new Vector3D(516,354),new Vector3D(513,367),new Vector3D(511,385),
					new Vector3D(509,397),new Vector3D(505,410),new Vector3D(502,424),new Vector3D(491,432),
					new Vector3D(471,435),new Vector3D(453,434),new Vector3D(439,434),new Vector3D(422,433),
					new Vector3D(400,433),new Vector3D(388,431),new Vector3D(367,430),new Vector3D(353,429),
					new Vector3D(332,430),new Vector3D(317,427),new Vector3D(311,413),new Vector3D(309,390),
					new Vector3D(308,386),new Vector3D(307,370),new Vector3D(308,352),new Vector3D(310,335),
					new Vector3D(311,306),new Vector3D(312,289),new Vector3D(310,273),new Vector3D(309,259),
					new Vector3D(309,243),new Vector3D(311,226),new Vector3D(315,209),new Vector3D(316,200),
					new Vector3D(320,180),new Vector3D(323,164),new Vector3D(331,157),new Vector3D(332,158),
			};
			points = Arrays.asList(point20_array);
			break;
			

		default: break;
		}

		templates.add(new Template(gesture, points, direction));
	}
}
