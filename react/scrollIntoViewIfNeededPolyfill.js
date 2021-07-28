/* eslint-disable no-param-reassign */
if (!Element.prototype.scrollIntoViewIfNeeded) {
  Element.prototype.scrollIntoViewIfNeeded = function (centerIfNeeded) {
    function makeRange(start, length) {
      return { start, length, end: start + length };
    }

    function coverRange(inner, outer) {
      if (centerIfNeeded === false
              || (outer.start < inner.end && inner.start < outer.end)) {
        return Math.min(
          inner.start, Math.max(outer.start, inner.end - outer.length),
        );
      }
      return (inner.start + inner.end - outer.length) / 2;
    }

    function makePoint(x, y) {
      return {
        x,
        y,
        translate: function translate(dX, dY) {
          return makePoint(x + dX, y + dY);
        },
      };
    }

    function absolute(elem, pt) {
      while (elem) {
        pt = pt.translate(elem.offsetLeft, elem.offsetTop);
        elem = elem.offsetParent;
      }
      return pt;
    }

    let target = absolute(this, makePoint(0, 0));
    const extent = makePoint(this.offsetWidth, this.offsetHeight);
    let elem = this.parentNode;
    let origin;

    while (elem instanceof HTMLElement) {
      // Apply desired scroll amount.
      origin = absolute(elem, makePoint(elem.clientLeft, elem.clientTop));
      elem.scrollLeft = coverRange(
        makeRange(target.x - origin.x, extent.x),
        makeRange(elem.scrollLeft, elem.clientWidth),
      );
      elem.scrollTop = coverRange(
        makeRange(target.y - origin.y, extent.y),
        makeRange(elem.scrollTop, elem.clientHeight),
      );

      // Determine actual scroll amount by reading back scroll properties.
      target = target.translate(-elem.scrollLeft, -elem.scrollTop);
      elem = elem.parentNode;
    }
  };
}
