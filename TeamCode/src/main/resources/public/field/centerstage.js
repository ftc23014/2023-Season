const INCHES_PER_PIXEL = 3.5;

const CENTER_STAGE_STANDARDS = {
    tile: new Unit(23 + (1/8), Unit.Type.INCHES),
    half_tile(n=1) {
        return new Unit((this.tile.get(Unit.Type.INCHES) / 2) * n, Unit.Type.INCHES);
    },
    width() {
        return new Unit(this.tile.get(Unit.Type.INCHES) * 6, Unit.Type.INCHES)
    },
    height() {
        return this.width();
    },
    half_width() {
        return new Unit(this.width().get(Unit.Type.INCHES) / 2, Unit.Type.INCHES);
    },
    half_height() {
        return this.half_width();
    }
}

const CENTER_STAGE = {
    tile: CENTER_STAGE_STANDARDS.tile,
    width: CENTER_STAGE_STANDARDS.width(),
    height: CENTER_STAGE_STANDARDS.height(),
    /**
     * All measurements are in inches unless specified in the units.
     * All measurements are relative to the top left corner of the field,
     * as in the blue alliance backdrop side in the top left and the red alliance backdrop side in the top right.
     *
     * Standard:
     * • Use units in all measurements unless specified otherwise.
     * • Use the top left corner of the field as the origin.
     * • Lines are a list of unit pairs, where the first unit is the x coordinate and the second unit is the y coordinate.
     * • Lines are drawn in the order they are specified and must have the color specified in the name.
     * • Game elements are functions that must accept a CanvasRenderingContext2D as the first argument.
     */
    lines: {
        backstage_blue: [
            [
                new Unit(0, Unit.Type.INCHES),
                new Unit(23 + (1/8), Unit.Type.INCHES),
            ],
            [
                new Unit(56.25, Unit.Type.INCHES),
                new Unit(23 + (1/8), Unit.Type.INCHES),
            ],
            [
                new Unit(139/2, Unit.Type.INCHES),
                new Unit(0, Unit.Type.INCHES),
            ]
        ],
        backstage_red: [
            [
                CENTER_STAGE_STANDARDS.width(),
                new Unit(23 + (1/8), Unit.Type.INCHES),
            ],
            [
                Unit.add(CENTER_STAGE_STANDARDS.width(), new Unit(-56.25, Unit.Type.INCHES)),
                new Unit(23 + (1/8), Unit.Type.INCHES),
            ],
            [
                Unit.add(CENTER_STAGE_STANDARDS.width(), new Unit(-(139/2), Unit.Type.INCHES)),
                new Unit(0, Unit.Type.INCHES),
            ],
        ],
        blue_spike_frontstage1: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile(-1)
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(2)
                )
            ]
        ],
        blue_spike_frontstage2: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(1/4 * 2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(3/4 * 2)
                )
            ],
        ],
        blue_spike_frontstage3: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile(-1)
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile
                )
            ]
        ],
        red_spike_frontstage1: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile()
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(2)
                )
            ]
        ],
        red_spike_frontstage2: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(1/4 * 2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile,
                    CENTER_STAGE_STANDARDS.half_tile(3/4 * 2)
                )
            ],
        ],
        red_spike_frontstage3: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile()
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.tile
                )
            ]
        ],

        blue_spike_backstage1: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile(-1)
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-2)
                )
            ]
        ],
        blue_spike_backstage2: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-3/4 * 2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-1/4 * 2)
                )
            ],
        ],
        blue_spike_backstage3: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(-47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile(-1)
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                )
            ]
        ],
        red_spike_backstage1: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile()
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-2)
                )
            ]
        ],
        red_spike_backstage2: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-3/4 * 2)
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                    CENTER_STAGE_STANDARDS.half_tile(-1/4 * 2)
                )
            ],
        ],
        red_spike_backstage3: [
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                )
            ],
            [
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_width(),
                    new Unit(47/2, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile()
                ),
                Unit.add(
                    CENTER_STAGE_STANDARDS.half_height(),
                )
            ]
        ],

        blue_community_zone: [
            [
                Unit.add(CENTER_STAGE_STANDARDS.width(), CENTER_STAGE_STANDARDS.half_tile(-2)),
                CENTER_STAGE_STANDARDS.height()
            ],
            [
                CENTER_STAGE_STANDARDS.width(),
                Unit.add(CENTER_STAGE_STANDARDS.height(), CENTER_STAGE_STANDARDS.half_tile(-2)),
            ]
        ],
        red_community_zone: [
            [
                CENTER_STAGE_STANDARDS.half_tile(2),
                CENTER_STAGE_STANDARDS.height()
            ],
            [
                Unit.Zero(),
                Unit.add(CENTER_STAGE_STANDARDS.height(), CENTER_STAGE_STANDARDS.half_tile(-2)),
            ]
        ],
    },
    game_elements: {
        backdrop_blue(ctx=new CanvasRenderingContext2D()) {
            ctx.fillStyle = "#252525";

            ctx.fillRect(
                new Unit(22 + (1/2), Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                0,
                new Unit(25 + (5/8), Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                new Unit(11 + 1/4, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
            );
        },
        backdrop_red(ctx=new CanvasRenderingContext2D()) {
            ctx.fillStyle = "#252525";

            ctx.fillRect(
                Unit.add(CENTER_STAGE_STANDARDS.width(), new Unit(-(22 + (1/2)), Unit.Type.INCHES), new Unit(-(25 + (5/8)), Unit.Type.INCHES)).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                0,
                new Unit(25 + (5/8), Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                new Unit(11 + 1/4, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
            );
        },
        truss(ctx=new CanvasRenderingContext2D()) {
            const truss_support = new Unit(3/4, Unit.Type.INCHES);
            const truss_width = new Unit(1 + (5/16), Unit.Type.INCHES);

            const truss_y = new Unit(CENTER_STAGE_STANDARDS.tile.get(Unit.Type.INCHES) * 3.5, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);

            ctx.fillStyle = "#0000ff";
            ctx.fillRect(
                truss_support.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_y,
                new Unit(21 + 1/2, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            );

            ctx.fillRect(
                Unit.add(truss_support, new Unit(21 + 1/2, Unit.Type.INCHES), truss_support).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_y,
                new Unit(22 + 1/2, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            )

            ctx.fillStyle = "#ffff00"

            ctx.fillRect(
                Unit.add(truss_support, new Unit(21 + 22 + 1, Unit.Type.INCHES), truss_support, truss_support).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_y,
                new Unit(46 + 1/4, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            )

            ctx.fillStyle = "#ff0000"
            ctx.fillRect(
                Unit.add(truss_support, new Unit(21 + 22 + 46 + 1 + 1/4, Unit.Type.INCHES), truss_support, truss_support, truss_support).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_y,
                new Unit(22 + 1/2, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            )

            ctx.fillRect(
                Unit.add(truss_support, new Unit(21 + 44 + 46 + 1 + 3/4, Unit.Type.INCHES), truss_support, truss_support, truss_support, truss_support).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_y,
                new Unit(21 + 1/2, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
            )

            ctx.fillStyle = "#c0c0c0";
            let msrs = [
                21 + 1/2,
                22 + 1/2,
                46 + 1/4,
                22 + 1/2,
                21 + 1/2,
                0
            ]

            let x = 0;
            for (let i = 0; i < 6; i++) {
                ctx.fillRect(
                    Unit.add(new Unit(x, Unit.Type.INCHES)).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    truss_y + CENTER_STAGE_STANDARDS.half_tile(-1).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    truss_width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                    CENTER_STAGE_STANDARDS.half_tile(2).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
                )

                x += msrs[i] + (i == 0 || i == 2 ? truss_width.get(Unit.Type.INCHES) : 0);
            }
        }
    }
}

function centerStageInit() {
    if (fieldCanvasDOM) {
        fieldCanvasDOM.width = CENTER_STAGE.width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);
        fieldCanvasDOM.height = CENTER_STAGE.height.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);
    }
}

function drawCenterStage(ctx=new CanvasRenderingContext2D()) {
    ctx.clearRect(0, 0, CENTER_STAGE.width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES), CENTER_STAGE.height.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES));

    ctx.fillStyle = "#5C8374"; //"#afafaf";
    ctx.fillRect(0, 0, CENTER_STAGE.width.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES), CENTER_STAGE.height.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES));

    ctx.lineWidth = 1;
    for (let x = 0; x < 6; x++) {
        for (let y = 0; y < 6; y++) {
            ctx.strokeStyle = "#000000"
            ctx.rect(
                CENTER_STAGE.tile.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) * x,
                CENTER_STAGE.tile.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES) * y,
                CENTER_STAGE.tile.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES),
                CENTER_STAGE.tile.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES)
            );
            ctx.stroke();
        }
    }

    for (let game_element in CENTER_STAGE.game_elements) {
        CENTER_STAGE.game_elements[game_element](ctx);
    }

    let i = 0;
    for (let line_name in CENTER_STAGE.lines) {
        let line = CENTER_STAGE.lines[line_name];

        ctx.beginPath();

        if (line_name.includes("blue")) {
            ctx.strokeStyle = "#0000FF";
        } else if (line_name.includes("red")) {
            ctx.strokeStyle = "#FF0000";
        } else {
            ctx.strokeStyle = "#000000";
        }

        ctx.lineWidth = new Unit(1, Unit.Type.INCHES).getcu(INCHES_PER_PIXEL, Unit.Type.INCHES);

        let first = true;
        for (let point of line) {
            const x = point[0];
            const y = point[1];
            if (first) {
                ctx.moveTo(x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES), y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES));
                first = false;
                continue;
            }
            ctx.lineTo(x.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES), y.getcu(INCHES_PER_PIXEL, Unit.Type.INCHES));
        }
        ctx.stroke();

        i++;
    }
}