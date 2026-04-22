import TileButton from "@/app/Components/TileButton";
import {ResponseYaku} from "@/app/Fetcher";
import EmptyTile from "@/app/Components/EmptyTile";

interface YakuItemProps {
    yaku: ResponseYaku,
    last: boolean
}


export default function YakuItem({yaku, last}: YakuItemProps) {

    function nameTag() {
        if (yaku.englishName) {
            return (
                <h1 className="text-2xl text-white font-bold pb-1 text-wrap">
                    {yaku.englishName} | {yaku.japaneseName}:
                </h1>
            )
        } else {
            return (
                <h1 className="text-2xl text-white font-bold pb-1 text-wrap">
                    {yaku.japaneseName}:
                </h1>
            )
        }

    }

    function han() {
        if (yaku.han == 13) {
            return "Yakuman"
        } else if (yaku.han == 26) {
            return "Double Yakuman"
        } else {
            return `${yaku.han} Han`
        }
    }

    function tiles() {
        if (yaku.tiles && yaku.tiles.length > 0) {
            return (
                <div className="flex gap-1 p-3 bg-green-400 rounded-xl max-w-fit">
                    {yaku.tiles.map((tile, i) => {
                        return <TileButton face={tile} whenClicked={() => {}} inactive={true} key={i}/>
                    })}
                    {Array.from(
                        {length: 14 - yaku.tiles.length},
                        (_, key) => <EmptyTile key={key}/>
                    )}
                </div>
            )
        }
    }

    function divider() {
        return (
            <div className="bg-green-500 min-h-1 min-w-full mt-3 rounded-full"/>
        )
    }

    return (
        <div className="max-w-140 min-w-100 p-2">
            <div>
                {nameTag()}
                <h1 className="text-1xl text-white font-bold pb-1 text-wrap">
                    {yaku.description}
                </h1>
                <h1 className="text-2xl text-white font-bold pb-1 text-end min-w-full">{han()}</h1>
                {!last ? divider() : null}
            </div>
        </div>
    )
}