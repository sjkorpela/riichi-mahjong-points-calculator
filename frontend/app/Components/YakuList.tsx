import YakuItem from "@/app/Components/YakuItem";
import {YakuResponse} from "@/app/Fetcher";
import "@/app/CSS/Page.css";

interface YakuListProps {
    response: YakuResponse | null;
}

export default function YakuList({response}: YakuListProps) {

    if (!response) {return null; }
    if (!response.yaku || response.yaku.length == 0) { return null;}

    function scoreToTitle() {
        if (!response) { return null; }

        if (response.han >= 13) { // Yakuman
            return "Yakuman";
        } else if (response.han >= 11) { // Sanbaiman
            return "Sanbaiman";
        } else if (response.han >= 8)  { // Baiman
            return "Baiman"
        } else if (response.han >= 6) { // Haneman
            return "Haneman";
        } else if (response.han >= 5) { // Mangan
            return "Mangan";
        } else {
            return null;
        }
    }



    return (
        <div className="my-box flex flex-col gap-1">
            <h1 className="text-3xl text-white font-bold pb-1 text-center">Yaku</h1>
            <div className="flex justify-between">
                <div>
                    <h1 className="box-title">Han: {response.han}</h1>
                    <h1 className="box-title">Fu: {response.fu}</h1>
                    <h1 className="box-title">Score: {response.score}</h1>
                </div>
                <div className="flex items-center">
                    <h1 className="text-3xl text-white font-bold pb-1 text-center">{scoreToTitle()}</h1>
                </div>
            </div>


            {response.yaku.map((yakuItem, key) => (
                <YakuItem yaku={yakuItem} key={key} last={key == response.yaku.length - 1}/>
            ))}
        </div>
    )
}