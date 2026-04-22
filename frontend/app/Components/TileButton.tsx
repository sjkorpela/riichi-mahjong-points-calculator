import {GetFaceVerbose} from "@/app/Util";


interface TileButtonProps {
    face?: string,
    whenClicked: (event: MouseEvent) => void,
    inactive?: boolean,
    big?: boolean,
}

import "@/app/CSS/Tiles.css";
import { MouseEvent } from "react";
import Image from 'next/image'

export default function TileButton({face, whenClicked, inactive, big}: TileButtonProps) {
    function tile(inactive?: boolean) {
        let colors = "bg-white border-b-orange-300 hover:border-b-amber-500 active:bg-neutral-300 active:border-b-orange-400";
        const add = big? "big" : "";
        if (inactive) { colors = "bg-white border-b-gray-400"; }

        face = face ?? "xx";
        const faceVerbose = GetFaceVerbose(face);

        return (
            <div className={`tile ${colors} ${add}`}>
                <Image src={`/images/${face}.svg`} alt={faceVerbose} width={48} height={60} className="pointer-events-none w-full h-auto"/>
            </div>
        )
    }

    if (inactive) {
        return tile(inactive = true);
    } else {
        return (
            <button onClick={whenClicked} >
                {tile()}
            </button>
        )
    }
}