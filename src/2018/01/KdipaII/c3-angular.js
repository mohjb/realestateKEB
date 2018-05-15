/*! c3-angular - v0.3.1 - 2015-05-07
 * https://github.com/jettro/c3-angular-sample
 * Copyright (c) 2015 ; Licensed  */

angular.module("gridshore.c3js.chart", []).controller("ChartController", ["$scope", function (a) {
			function b() {
				a.chart = null,
				a.columns = [],
				a.types = {},
				a.axis = {},
				a.axes = {},
				a.padding = null,
				a.xValues = null,
				a.xsValues = null,
				a.xTick = null,
				a.yTick = null,
				a.names = null,
				a.colors = null,
				a.grid = null,
				a.legend = null,
				a.tooltip = null,
				a.chartSize = null,
				a.colors = null,
				a.gauge = null,
				a.jsonKeys = null,
				a.groups = null
			}
			function c(b, c, d, e) {
				void 0 !== c && (a.types[b] = c),
				void 0 !== d && (null === a.names && (a.names = {}), a.names[b] = d),
				void 0 !== e && (null === a.colors && (a.colors = {}), a.colors[b] = e)
			}
			function d() {
				a.jsonKeys = {},
				a.jsonKeys.value = [],
				angular.forEach(a.chartColumns, function (b) {
					a.jsonKeys.value.push(b.id),
					c(b.id, b.type, b.name, b.color)
				}),
				a.chartX && (a.jsonKeys.x = a.chartX.id),
				a.names && (a.config.data.names = a.names),
				a.colors && (a.config.data.colors = a.colors),
				a.groups && (a.config.data.groups = a.groups),
				a.config.data.keys = a.jsonKeys,
				a.config.data.json = a.chartData,
				main.addChart(a.chart = c3.generate(a.config))
			}
			b(),
			this.showGraph = function () {
				var c = {};
				c.bindto = "#" + a.bindto,
				c.data = {},
				a.xValues && (c.data.x = a.xValues),
				a.xsValues && (c.data.xs = a.xsValues),
				a.columns && (c.data.columns = a.columns),
				c.data.types = a.types,
				c.data.axes = a.axes,
				a.names && (c.data.names = a.names),
				null != a.padding && (c.padding = a.padding),
				a.colors && (c.data.colors = a.colors),
				a.colorFunction && (c.data.color = a.colorFunction),
				a.showLabels && "true" === a.showLabels && (c.data.labels = !0),
				null != a.groups && (c.data.groups = a.groups),
				a.showSubchart && "true" === a.showSubchart && (c.subchart = {
						show: !0
					}),
				a.enableZoom && "true" === a.enableZoom && (c.zoom = {
						enabled: !0
					}),
				c.axis = a.axis,
				a.xTick && (c.axis.x.tick = a.xTick),
				a.yTick && (c.axis.y.tick = a.yTick),
				null != a.grid && (c.grid = a.grid),
				null != a.legend && (c.legend = a.legend),
				null != a.tooltip && (c.tooltip = a.tooltip),
				null != a.chartSize && (c.size = a.chartSize),
				null != a.colors && (c.color = {
						pattern: a.colors
					}),
				null != a.gauge && (c.gauge = a.gauge),
				null != a.point && (c.point = a.point),
				null != a.bar && (c.bar = a.bar),
				null != a.pie && (c.pie = a.pie),
				null != a.donut && (c.donut = a.donut),
				null != a.onInit && (c.oninit = a.onInit),
				null != a.onMouseover && (c.onmouseover = a.onMouseover),
				null != a.onMouseout && (c.onmouseout = a.onMouseout),
				null != a.onRendered && (c.onrendered = a.onRendered),
				null != a.onResize && (c.onresize = a.onResize),
				null != a.onResized && (c.onresized = a.onResized),
				null != a.dataOnClick && (c.data.onclick = function (b) {
					a.$apply(function () {
						a.dataOnClick({
							data: b
						})
					})
				}),
				null != a.dataOnMouseover && (c.data.onmouseover = function (b) {
					a.$apply(function () {
						a.dataOnMouseover({
							data: b
						})
					})
				}),
				null != a.dataOnMouseout && (c.data.onmouseout = function (b) {
					a.$apply(function () {
						a.dataOnMouseout({
							data: b
						})
					})
				}),
				a.config = c,
				a.chartData && a.chartColumns ? a.$watchCollection("chartData", function () {
					d()
				}) : main.addChart(a.chart = c3.generate(a.config)),
				a.$on("$destroy", function () {
					angular.isDefined(a.chart) && (a.chart = a.chart.destroy(), b())
				})
			},
			this.addColumn = function (b, d, e, f) {
				a.columns.push(b),
				c(b[0], d, e, f)
			},
			this.addYAxis = function (b) {
				a.axes = b,
				a.axis.y2 || (a.axis.y2 = {
						show: !0
					})
			},
			this.addXAxisValues = function (b) {
				a.xValues = b
			},
			this.addXSValues = function (b) {
				a.xsValues = b
			},
			this.addAxisProperties = function (b, c) {
				a.axis[b] = c
			},
			this.addXTick = function (b) {
				a.xTick = b
			},
			this.addYTick = function (b) {
				a.yTick = b
			},
			this.rotateAxis = function () {
				a.axis.rotated = !0
			},
			this.addPadding = function (b, c) {
				null == a.padding && (a.padding = {}),
				a.padding[b] = parseInt(c)
			},
			this.addGrid = function (b) {
				null == a.grid && (a.grid = {}),
				null == a.grid[b] && (a.grid[b] = {}),
				a.grid[b].show = !0
			},
			this.addGridLine = function (b, c, d) {
				null == a.grid && (a.grid = {}),
				"x" === b ? (void 0 === a.grid.x && (a.grid.x = {}), void 0 === a.grid.x.lines && (a.grid.x.lines = [])) : (void 0 === a.grid.y && (a.grid.y = {}), void 0 === a.grid.y.lines && (a.grid.y.lines = [])),
				"y2" === b ? a.grid.y.lines.push({
					value: c,
					text: d,
					axis: "y2"
				}) : a.grid[b].lines.push({
					value: c,
					text: d
				})
			},
			this.addLegend = function (b) {
				a.legend = b
			},
			this.addTooltip = function (b) {
				a.tooltip = b
			},
			this.addSize = function (b) {
				a.chartSize = b
			},
			this.addColors = function (b) {
				a.colors = b
			},
			this.addColorFunction = function (b) {
				a.colorFunction = b
			},
			this.addOnInitFunction = function (b) {
				a.onInit = b
			},
			this.addOnMouseoverFunction = function (b) {
				a.onMouseover = b
			},
			this.addOnMouseoutFunction = function (b) {
				a.onMouseout = b
			},
			this.addOnRenderedFunction = function (b) {
				a.onRendered = b
			},
			this.addOnResizeFunction = function (b) {
				a.onResize = b
			},
			this.addOnResizedFunction = function (b) {
				a.onResized = b
			},
			this.addDataOnClickFunction = function (b) {
				a.dataOnClick = b
			},
			this.addDataOnMouseoverFunction = function (b) {
				a.dataOnMouseover = b
			},
			this.addDataOnMouseoutFunction = function (b) {
				a.dataOnMouseout = b
			},
			this.addGauge = function (b) {
				a.gauge = b
			},
			this.addBar = function (b) {
				a.bar = b
			},
			this.addPie = function (b) {
				a.pie = b
			},
			this.addDonut = function (b) {
				a.donut = b
			},
			this.addGroup = function (b) {
				null == a.groups && (a.groups = []),
				a.groups.push(b)
			},
			this.addPoint = function (b) {
				a.point = b
			},
			this.hideGridFocus = function () {
				null == a.grid && (a.grid = {}),
				a.grid.focus = {
					show: !1
				}
			}
		}
	]).directive("c3chart", ["$timeout", function (a) {
			var b = function (b, c, d, e) {
				var f = d.paddingTop,
				g = d.paddingRight,
				h = d.paddingBottom,
				i = d.paddingLeft;
				f && e.addPadding("top", f),
				g && e.addPadding("right", g),
				h && e.addPadding("bottom", h),
				i && e.addPadding("left", i),
				a(function () {
					e.showGraph()
				})
			};
			return {
				restrict: "E",
				controller: "ChartController",
				scope: {
					bindto: "@bindtoId",
					showLabels: "@showLabels",
					showSubchart: "@showSubchart",
					enableZoom: "@enableZoom",
					chartData: "=chartData",
					chartColumns: "=chartColumns",
					chartX: "=chartX"
				},
				template: "<div><div id='{{bindto}}'></div><div ng-transclude></div></div>",
				replace: !0,
				transclude: !0,
				link: b
			}
		}
	]).directive("chartColumn", function () {
	var a = function (a, b, c, d) {
		var e = c.columnValues.split(",");
		e.unshift(c.columnId),
		d.addColumn(e, c.columnType, c.columnName, c.columnColor)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartAxes", function () {
	var a = function (a, b, c, d) {
		var e = c.valuesX;
		e && d.addXAxisValues(e);
		var f = c.valuesXs,
		g = {};
		if (f) {
			xsItems = f.split(",");
			for (var h in xsItems)
				xsItem = xsItems[h].split(":"), g[xsItem[0]] = xsItem[1];
			d.addXSValues(g)
		}
		var i = c.y,
		j = c.y2,
		k = {};
		if (j) {
			var l = j.split(",");
			for (var m in l)
				k[l[m]] = "y2";
			if (i) {
				var n = i.split(",");
				for (var o in n)
					k[n[o]] = "y"
			}
			d.addYAxis(k)
		}
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartAxis", function () {
	var a = function (a, b, c, d) {
		var e = c.axisRotate;
		e && d.rotateAxis()
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		transclude: !0,
		template: "<div ng-transclude></div>",
		replace: !0,
		link: a
	}
}).directive("chartAxisX", function () {
	var a = function (a, b, c, d) {
		var e = c.axisPosition,
		f = c.axisLabel,
		g = {
			label: {
				text: f,
				position: e
			}
		},
		h = c.axisType;
		h && (g.type = h);
		var i = c.paddingLeft,
		j = c.paddingRight;
		(i || j) && (i = i ? i : 0, j = j ? j : 0, g.padding = {
				left: parseInt(i),
				right: parseInt(j)
			}),
		"false" === c.show && (g.show = !1),
		"true" === c.axisLocaltime && (g.localtime = !0);
		var k = c.axisMax;
		k && (g.max = k);
		var l = c.axisMin;
		l && (g.min = l),
		d.addAxisProperties("x", g)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		transclude: !0,
		template: "<div ng-transclude></div>",
		replace: !0,
		link: a
	}
}).directive("chartAxisY", function () {
	var a = function (a, b, c, d) {
		var e = c.axisId,
		f = c.axisPosition,
		g = c.axisLabel;
		e = void 0 == e ? "y" : e;
		var h = {
			label: {
				text: g,
				position: f
			}
		};
		"false" === c.show ? h.show = !1 : "y2" === e && (h.show = !0);
		var i = c.paddingTop,
		j = c.paddingBottom;
		(i || j) && (i = i ? i : 0, j = j ? j : 0, h.padding = {
				top: parseInt(i),
				bottom: parseInt(j)
			});
		var k = c.axisMax,
		l = c.axisMin;
		k && (h.max = parseInt(k)),
		l && (h.min = parseInt(l)),
		"true" === c.axisInverted && (h.inverted = !0),
		d.addAxisProperties(e, h)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartGrid", function () {
	var a = function (a, b, c, d) {
		var e = c.showX;
		e && "true" === e && d.addGrid("x");
		var f = c.showY;
		f && "true" === f && d.addGrid("y");
		var g = c.showY2;
		g && "true" === g && d.addGrid("y2");
		var h = c.showFocus;
		h && "false" === h && d.hideGridFocus()
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a,
		transclude: !0,
		template: "<div ng-transclude></div>"
	}
}).directive("chartGridOptional", function () {
	var a = function (a, b, c, d) {
		var e = c.axisId,
		f = c.gridValue,
		g = c.gridText;
		d.addGridLine(e, f, g)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartAxisXTick", function () {
	var a = function (a, b, c, d) {
		var e = {},
		f = c.tickCount;
		f && (e.count = f);
		var g = c.tickFormat;
		g && (e.format = g);
		var h = c.tickCulling;
		h && (h = angular.lowercase(h), "true" === h ? e.culling = !0 : "false" === h && (e.culling = !1));
		var i = c.tickRotate;
		i && (e.rotate = i);
		var j = c.tickFit;
		j && (j = angular.lowercase(j), "true" === j ? e.fit = !0 : "false" === j && (e.fit = !1)),
		d.addXTick(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartAxisYTick", function () {
	var a = function (a, b, c, d) {
		var e = {},
		f = c.tickCount;
		f && (e.count = f);
		var g = c.tickFormat;
		g && (e.format = d3.format(g)),
		d.addYTick(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartLegend", function () {
	var a = function (a, b, c, d) {
		var e = null,
		f = c.showLegend;
		if (f && "false" === f)
			e = {
				show: !1
			};
		else {
			var g = c.legendPosition;
			g && (e = {
					position: g
				});
			var h = c.legendInset;
			h && (e = {
					position: "inset",
					inset: {
						anchor: h
					}
				})
		}
		null != e && d.addLegend(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartTooltip", function () {
	var a = function (a, b, c, d) {
		var e = null,
		f = c.showTooltip;
		if (f && "false" === f)
			e = {
				show: !1
			};
		else {
			var g = c.groupTooltip;
			g && "false" === g && (e = {
					grouped: !1
				})
		}
		null != e && d.addTooltip(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartSize", function () {
	var a = function (a, b, c, d) {
		var e = null,
		f = c.chartWidth,
		g = c.chartHeight;
		(f || g) && (e = {}, f && (e.width = parseInt(f)), g && (e.height = parseInt(g)), d.addSize(e))
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartColors", function () {
	var a = function (a, b, c, d) {
		var e = c.colorPattern;
		e && d.addColors(e.split(",")),
		c.colorFunction && d.addColorFunction(a.colorFunction())
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {
			colorFunction: "&"
		},
		replace: !0,
		link: a
	}
}).directive("chartGroup", function () {
	var a = function (a, b, c, d) {
		var e = c.groupValues.split(",");
		d.addGroup(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartGauge", function () {
	var a = function (a, b, c, d) {
		var e = {};
		c.min && (e.min = parseInt(c.min)),
		c.max && (e.max = parseInt(c.max)),
		c.width && (e.width = parseInt(c.width)),
		c.units && (e.units = c.units),
		c.showLabel && (e.label = {
				show: "true" === c.showLabel
			}),
		c.expand && (e.expand = "true" === c.expand),
		d.addGauge(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartPoints", function () {
	var a = function (a, b, c, d) {
		var e = {};
		c.showPoint && (e.show = "true" === c.showPoint),
		c.pointExpandEnabled && (e.focus || (e.focus = {
					expand: {}
				}), e.focus.expand.enabled = "false" !== c.pointsFocusEnabled),
		c.pointExpandRadius && (e.focus || (pie.focus = {
					expand: {}
				}), e.focus.expand.r = parseInt(c.pointFocusRadius)),
		c.pointRadius && (e.r = parseInt(c.pointRadius)),
		c.pointSelectRadius && (e.select = {
				r: parseInt(c.pointSelectRadius)
			}),
		d.addPoint(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartPie", function () {
	var a = function (a, b, c, d) {
		var e = {};
		c.showLabel && (e.label = {
				show: "true" === c.showLabel
			}),
		c.thresholdLabel && (e.label || (e.label = {}), e.label.threshold = parseFloat(c.thresholdLabel)),
		c.expand && (e.expand = "true" === c.expand),
		d.addPie(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartDonut", function () {
	var a = function (a, b, c, d) {
		var e = {};
		c.showLabel && (e.label = {
				show: "true" === c.showLabel
			}),
		c.thresholdLabel && (e.label || (e.label = {}), e.label.threshold = parseFloat(c.thresholdLabel)),
		c.expand && (e.expand = "true" === c.expand),
		c.width && (e.width = parseInt(c.width)),
		c.title && (e.title = c.title),
		d.addDonut(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartBar", function () {
	var a = function (a, b, c, d) {
		var e = {};
		c.width && (e.width = parseInt(c.width)),
		c.ratio && (e.width || (e.width = {}), e.width.ratio = parseFloat(c.ratio)),
		c.zerobased && (e.zerobased = "true" === c.zerobased),
		d.addBar(e)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {},
		replace: !0,
		link: a
	}
}).directive("chartEvents", function () {
	var a = function (a, b, c, d) {
		c.onInit && d.addOnInitFunction(a.onInit),
		c.onMouseover && d.addOnMouseoverFunction(a.onMouseover),
		c.onMouseout && d.addOnMouseoutFunction(a.onMouseout),
		c.onResize && d.addOnResizeFunction(a.onResize),
		c.onResized && d.addOnResizedFunction(a.onResized),
		c.onRendered && d.addOnRenderedFunction(a.onRendered),
		c.onClickData && d.addDataOnClickFunction(a.onClickData),
		c.onMouseoverData && d.addDataOnMouseoverFunction(a.onMouseoverData),
		c.onMouseoutData && d.addDataOnMouseoutFunction(a.onMouseoutData)
	};
	return {
		require: "^c3chart",
		restrict: "E",
		scope: {
			onInit: "&",
			onMouseover: "&",
			onMouseout: "&",
			onResize: "&",
			onResized: "&",
			onRendered: "&",
			onClickData: "&",
			onMouseoverData: "&",
			onMouseoutData: "&"
		},
		replace: !0,
		link: a
	}
});
//# sourceMappingURL=c3-angular.min.js.map