"use client";

const tileTypes = ["s1", "s2", "s3", "s4", "s5", "s5r", "s6", "s7", "s8", "s9", "m1", "m2", "m3", "m4", "m5", "m5r", "m6", "m7", "m8", "m9", "p1", "p2", "p3", "p4", "p5", "p5r", "p6", "p7", "p8", "p9", "we", "ws", "ww", "wn", "dg", "dr", "dw", ];

// better way to do this??
const allTiles: {[index: string]: number} = {};
tileTypes.forEach((tile) => {
    allTiles[tile] = 0;
})

const maxHandSize = 13;
const maxDora = 10;
const maxTileCount = 4;
const red5TileCount = 1;

import "@/app/CSS/Page.css";
import {useEffect, useEffectEvent, useState} from "react";
import Hand from "@/app/Components/Hand";
import WinningTile from "@/app/Components/WinningTile";
import TileButton from "@/app/Components/TileButton";
import ModeSelect from "@/app/Components/ModeSelect";
import FancyBoolean from "@/app/Components/FancyBoolean";
import WindSelect from "@/app/Components/WindSelect";
import Boolean from "@/app/Components/Boolean";
import DoraIndicators from "@/app/Components/DoraIndicators";
import {Fetcher, YakuResponse} from "@/app/Fetcher";
import YakuList from "@/app/Components/YakuList";
import YakuListErrorMessage from "@/app/Components/YakuListErrorMessage";

export default function Home() {

    // region states & functions

    const [tileSelectFocus, setTileSelectFocus] = useState<string | null>("H");

    function focusFunc(focus: string | null) {
        switch (focus) {
            case "H":
                return addTileToHand;
            case "W":
                return addTileToWinning
            case "D":
                return addTileToDora;
            default:
                return () => {};
        }
    }

    const [hand, setHand] = useState<{[index: string]: number}>(allTiles)
    const [handSize, setHandSize] = useState<number>(0);
    const handFull = handSize >= maxHandSize;

    const [winningTile, setWinningTile] = useState<string | null>(null);

    const [dora, setDora] = useState<string[]>([])
    const doraFull = dora.length >= maxDora;

    const [spentTiles, setSpentTiles] = useState<{[index: string]: number}>(allTiles)

    const [roundWind, setRoundWind] = useState("we");
    const [seatWind, setSeatWind] = useState("we");

    const [tsumo, setTsumo] = useState<boolean>(false);
    const [openHand, setOpenHand] = useState<boolean>(false);
    const [riichi, setRiichi] = useState<boolean>(false);
    const [ippatsu, setIppatsu] = useState<boolean>(false);
    const [doubleRiichi, setDoubleRiichi] = useState<boolean>(false);
    const [lastTile, setLastTile] = useState<boolean>(false);
    const [afterKan, setAfterKan] = useState<boolean>(false);
    const [blessedHand, setBlessedHand] = useState<boolean>(false);

    const [response, setResponse] = useState<YakuResponse | null>(null);

    function addTileToHand(tile: string) {
        if (hand[tile] < maxTileCount && handSize < maxHandSize) {
            setHand({...hand, [tile]: hand[tile] + 1});
            setSpentTiles({...spentTiles, [tile]: spentTiles[tile] + 1});
            setHandSize(handSize + 1);

            if (handSize + 1 == maxHandSize && !winningTile) {
                setTileSelectFocus("W");
            }
        }
    }

    function removeTileFromHand(tile: string) {
        setHand({...hand, [tile]: hand[tile] - 1});
        setSpentTiles({...spentTiles, [tile]: spentTiles[tile] - 1});
        setHandSize(handSize - 1);
    }

    function addTileToWinning(tile: string) {
        if (!winningTile) {
            setWinningTile(tile);
            setSpentTiles({...spentTiles, [tile]: spentTiles[tile] + 1})
        }
    }

    function removeTileFromWinning(tile: string) {
        setWinningTile(null);
        setSpentTiles({...spentTiles, [tile]: spentTiles[tile] - 1});
    }

    function addTileToDora(tile: string) {
        if (dora.length < maxDora) {
            setSpentTiles({...spentTiles, [tile]: spentTiles[tile] + 1})
            setDora([...dora, tile]);
        }
    }

    function removeTileFromDora(index: number) {
        setSpentTiles({...spentTiles, [dora[index]]: spentTiles[dora[index]] - 1});
        setDora(dora => dora.filter((_, i) => (i != index)))
    }

    async function updateYaku(): Promise<void> {
        if (winningTile == null) { return; }

        const trimmedHand: {[index: string]: number} = {}
        Object.keys(hand).forEach((tile) => {
            if (hand[tile] > 0) {trimmedHand[tile] = hand[tile]}
        })

        const calculationInfo = {
            hand: trimmedHand,
            winningTile: winningTile,
            roundWind: roundWind,
            seatWind: seatWind,
            dora: dora,
            openHand: openHand,
            tsumo: tsumo,
            flags: {
                riichi: riichi,
                ippatsu: ippatsu,
                doubleRiichi: doubleRiichi,
                lastTile: lastTile,
                afterKan: afterKan,
                blessedHand: blessedHand,
            }
        }

        try {
            const result: YakuResponse = await Fetcher.getYaku(calculationInfo);
            console.log("Result: ", result);
            setResponse(result);
        } catch {
            setResponse(null);
        }
    }

    const onFullHand = useEffectEvent(() => {
        updateYaku();
    })

    useEffect(() => {
        if (handFull && winningTile != null) {
            onFullHand();
        }
    }, [handFull, spentTiles, winningTile, roundWind, seatWind, tsumo, openHand, riichi, ippatsu, doubleRiichi, lastTile, afterKan, blessedHand]);

    // endregion

    return (
        <div className="min-w-full bg-green-400 flex justify-center p-20">
            <div className="flex flex-col min-h-screen min-w-[900px] items-center justify-center gap-3 ">
                <div className="flex gap-3">

                    <div className={`my-box ${tileSelectFocus == "H" ? "selected" : "selectable"}`} onClick={() => setTileSelectFocus("H")}>
                        <h1 className="box-title pb-4">Hand {handSize}/{maxHandSize}</h1>
                        <Hand hand={hand} removeTileFromHand={removeTileFromHand} maxHandSize={maxHandSize} />
                    </div>
                    <div className={`flex flex-col items-center my-box ${tileSelectFocus == "W" ? "selected" : "selectable"}`} onClick={() => setTileSelectFocus("W")}>
                        <h1 className="box-title pb-1">Winning Tile</h1>
                        <WinningTile face={winningTile} removeTileFromWinning={removeTileFromWinning} />
                    </div>

                </div>


                <div className="flex flex-wrap items-start gap-3">
                    <div className="flex flex-col gap-3 max-w-[540px]">
                        <div className="my-box">
                            <div className="flex justify-between pb-4">
                                <h1 className="box-title">Tile Select</h1>
                                <ModeSelect options={["H", "W", "D"]} activeMode={tileSelectFocus} setMode={(mode) => setTileSelectFocus(mode)} />
                            </div>
                            <div className="grid grid-cols-10 gap-1">
                                {tileTypes.map((face, key) => {
                                    let noMoreTile: boolean
                                    // normally there's four copies of a tile,
                                    // but because one five of every land is a red five
                                    // there can only be 3 fives and 1 red five
                                    if (face[2] == 'r') { noMoreTile = spentTiles[face] >= red5TileCount; }
                                    else if (face[1] == '5') { noMoreTile = spentTiles[face] >= maxTileCount - red5TileCount; }
                                    else { noMoreTile = spentTiles[face] >= maxTileCount; }

                                    const addTile = focusFunc(tileSelectFocus);


                                    return <TileButton
                                        face={face}
                                        whenClicked={() => addTile(face)}
                                        key={key}
                                        inactive={(tileSelectFocus == "H" && handFull) || (tileSelectFocus == "W" && !!winningTile) || (tileSelectFocus == "D" && doraFull) || noMoreTile || !tileSelectFocus}
                                    />
                                })}
                            </div>
                        </div>
                        <div className={`my-box ${tileSelectFocus == "D" ? "selected" : "selectable"}`} onClick={() => setTileSelectFocus("D")}>
                            <h1 className="box-title pb-1">Dora</h1>
                            <DoraIndicators dora={dora} removeTileFromDora={removeTileFromDora} maxDora={maxDora}/>
                        </div>
                        <YakuListErrorMessage handFull={handFull} winningTile={winningTile != null} response={response} />
                        <YakuList response={response}/>
                    </div>
                    <div className="flex flex-col gap-3">
                        <div className="my-box justify-center">
                            <h1 className="box-title pb-1">Win Declaration</h1>
                            <FancyBoolean trueOption={"Tsumo"} falseOption={"Ron"} bool={tsumo} updateBool={() => setTsumo(!tsumo)} flipOptions={true}/>
                        </div>
                        <div className="my-box justify-center">
                            <h1 className="box-title pb-1">Hand State</h1>
                            <FancyBoolean trueOption={"Open"} falseOption={"Closed"} bool={openHand} updateBool={() => setOpenHand(!openHand)} flipOptions={true}/>
                        </div>
                        <div className="my-box">
                            <h1 className="box-title pb-1">Round Wind</h1>
                            <WindSelect wind={roundWind} updateWind={setRoundWind}/>
                            <h1 className="box-title pb-1">Seat Wind</h1>
                            <WindSelect wind={seatWind} updateWind={setSeatWind}/>
                        </div>
                        <div className="my-box">
                            <h1 className="box-title pb-1">Extra Han</h1>
                            <Boolean name={"Riichi"} bool={riichi} updateBool={() => setRiichi(!riichi)} blocked={openHand}/>
                            <Boolean name={"Ippatsu"} bool={ippatsu} updateBool={() => setIppatsu(!ippatsu)} blocked={openHand || !riichi}/>
                            <Boolean name={"Double Riichi"} bool={doubleRiichi} updateBool={() => setDoubleRiichi(!doubleRiichi)} blocked={openHand || !riichi}/>
                            <Boolean name={"Last Tile"} bool={lastTile} updateBool={() => setLastTile(!lastTile)}/>
                            <Boolean name={"After a Kan"} bool={afterKan} updateBool={() => setAfterKan(!afterKan)}/>
                            <Boolean name={"Blessed Hand"} bool={blessedHand} updateBool={() => setBlessedHand(!blessedHand)}/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
